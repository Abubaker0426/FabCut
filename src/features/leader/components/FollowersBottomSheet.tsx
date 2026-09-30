/**
 * FollowersBottomSheet
 * Mirrors: followers_jobs_bottom_sheet.xml + ViewPager behaviour in LeaderActivity
 *
 * Two pages — swipe left/right OR tap the tab pills:
 *  Page 0 — Followers grid  (3 columns)
 *  Page 1 — Assigned Jobs list
 *
 * Data is fetched here (not in the parent) because the sheet owns its lifecycle.
 * Mirrors Java: sheetBehavior.STATE_EXPANDED →
 *   mPresenter.fetchFollowers() + mPresenter.fetchAssignedJobs()
 *
 * Follower card tap  → onFollowerPress (parent navigates or assigns job)
 * Follower card X    → fetch job → confirm delete dialog → delete
 *   Java: onDelete(follower) → mPresenter.getFollowerJob(follower)
 *         → if jobId != null   → confirm → deleteJob(jobId)
 *         → if jobId == null   → getFollowerEndBitJob → confirm → deleteEndBitJob
 */

import BottomSheet from '@gorhom/bottom-sheet';
import { useEffect, useRef, useState } from 'react';
import { Alert, Pressable, ScrollView, Text, View } from 'react-native';
import PagerView from 'react-native-pager-view';
import { useRouter } from 'expo-router';

import AppSheetModal from '@/components/Common/AppSheetModal';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { useAppStore } from '@/store/appStore';
import { toast } from '@/lib/toast';
import type { Follower, OcLay } from '@/types/leader';
import type { FollowersBottomSheetProps } from '@/types/FollowersBottomSheet';

import { FollowerCard } from './FollowerCard';
import { JobListHeader, JobListRow } from './JobListRow';
import { useFetchFollowers } from '../hooks/useFetchFollowers';
import { useFetchAssignedJobs } from '../hooks/useFetchAssignedJobs';
import { useFetchFollowerJob } from '../hooks/useFetchFollowerJob';
import { useDeleteFollowerJob } from '../hooks/useDeleteFollowerJob';
import { useDeleteEndBitJob } from '../hooks/useDeleteFollowerJob';

const TAB_LABELS = ['Followers', 'Jobs Assigned'] as const;

export function FollowersBottomSheet({
  visible,
  onClose,
  selectionMode = false,
  onFollowerPress,
  onJobPress,
}: FollowersBottomSheetProps) {
  const router     = useRouter();
  const location   = useAppStore((s) => s.location);
  const deviceId   = useAppStore((s) => s.deviceId);
  const sheetRef   = useRef<BottomSheet>(null);
  const pagerRef   = useRef<PagerView>(null);
  const [activePage, setActivePage] = useState(0);

  // ── Sheet open/close ────────────────────────────────────────────────────────
  useEffect(() => {
    if (visible) {
      sheetRef.current?.snapToIndex(1);
    } else {
      sheetRef.current?.close();
    }
  }, [visible]);

  // ── Data hooks — enabled only when sheet is open ────────────────────────────
  // Mirrors Java: sheetBehavior.STATE_EXPANDED →
  //   mPresenter.fetchFollowers() + mPresenter.fetchAssignedJobs()
  const {
    data: followers,
    isLoading: isFollowersLoading,
    refetch: refetchFollowers,
  } = useFetchFollowers(location, visible);

  const {
    data: assignedJobs,
    isLoading: isJobsLoading,
    refetch: refetchJobs,
  } = useFetchAssignedJobs(location, visible);

  // ── Delete flow hooks ───────────────────────────────────────────────────────
  const { mutate: fetchFollowerJobMutate, isPending: isFetchingJob }    = useFetchFollowerJob();
  // Separate mutation instance for the tap-to-view flow
  const { mutate: fetchFollowerJobForView, isPending: isFetchingForView } = useFetchFollowerJob();
  const { mutate: deleteNormalJob,         isPending: isDeletingNormal }  = useDeleteFollowerJob();
  const { mutate: deleteEndBitJob,         isPending: isDeletingEndBit }  = useDeleteEndBitJob();

  const isActionLoading = isFetchingJob || isFetchingForView || isDeletingNormal || isDeletingEndBit;

  // ── Follower card tap — view job details ────────────────────────────────────
  // Mirrors Java: onItemClickListener(follower) → mPresenter.fetchFollowerJob(follower)
  //   → jobId != null   → openFollowerDetailActivity(response)       [NORMAL]
  //   → jobId == null   → fetchFollowerEndBitJob → openFollowerDetailActivityForEndBit [END_BIT]
  //   → endBitJobId == null → showAlertDialog("No job assigned")     [NONE]
  const handleFollowerTap = (follower: Follower) => {
    // In selection mode the parent handles the tap (assigning a job)
    if (selectionMode) {
      onFollowerPress?.(follower);
      return;
    }
    if (!location) return;

    fetchFollowerJobForView(
      { deviceId: follower.deviceId, location },
      {
        onSuccess: (result) => {
          if (result.type === 'NONE') {
            toast.info('No job assigned to this follower.');
            return;
          }
          sheetRef.current?.close();
          // Pass full response as JSON — mirrors Java's Intent.putExtras(serializable)
          router.push({
            pathname: '/leader/follower-details',
            params: {
              jobType:     result.type,
              responseJson: JSON.stringify(result.data),
            },
          });
        },
        onError: (err: any) => {
          toast.apiError(err, 'Error', 'Failed to fetch follower job.');
        },
      },
    );
  };
  // Mirrors Java: onDelete(follower) → mPresenter.getFollowerJob(follower)
  //   → if jobId != null   → confirm dialog → deleteJob(jobId)
  //   → if jobId == null   → getFollowerEndBitJob → confirm → deleteEndBitJob
  const handleDeletePress = (follower: Follower) => {
    if (!location) return;

    fetchFollowerJobMutate(
      { deviceId: follower.deviceId, location },
      {
        onSuccess: (result) => {
          if (result.type === 'NONE') {
            toast.info('No job assigned to this follower.');
            return;
          }

          // Mirrors Java: showAlertDialog(R.string.confirm_delete_job, ...)
          Alert.alert(
            'Delete Job',
            'Are you sure you want to delete the assigned job for this follower?',
            [
              { text: 'Cancel', style: 'cancel' },
              {
                text: 'Delete',
                style: 'destructive',
                onPress: () => {
                  if (result.type === 'NORMAL') {
                    // Mirrors Java: deleteJob(response.getJobId())
                    deleteNormalJob(
                      { jobId: String(result.data.jobId) },
                      {
                        onSuccess: () => {
                          toast.success('Job deleted successfully.');
                          refetchFollowers();
                          refetchJobs();
                        },
                        onError: (err: any) => {
                          toast.apiError(err, 'Error', 'Failed to delete job.');
                        },
                      },
                    );
                  } else {
                    // Mirrors Java: deleteEndBitJob(response.getEndBitJobId(), follower.getDeviceId())
                    deleteEndBitJob(
                      {
                        endBitJobId: String(result.data.endBitJobId),
                        data: { deviceId: follower.deviceId, location },
                      },
                      {
                        onSuccess: () => {
                          toast.success('End-bit job deleted successfully.');
                          refetchFollowers();
                          refetchJobs();
                        },
                        onError: (err: any) => {
                          toast.apiError(err, 'Error', 'Failed to delete end-bit job.');
                        },
                      },
                    );
                  }
                },
              },
            ],
          );
        },
        onError: (err: any) => {
          toast.apiError(err, 'Error', 'Failed to fetch follower job.');
        },
      },
    );
  };

  // ── Tab navigation ──────────────────────────────────────────────────────────
  const goToPage = (index: number) => {
    pagerRef.current?.setPage(index);
    setActivePage(index);
  };

  const followerList  = followers   ?? [];
  const jobList       = assignedJobs ?? [];

  return (
    <AppSheetModal
      ref={sheetRef}
      title={TAB_LABELS[activePage]}
      badge={activePage === 0 ? followerList.length : jobList.length}
      snapPoints={['7%', '55%', '85%']}
      initialIndex={-1}
      onClose={onClose}
      scrollable={false}
    >
      <LoadingOverlay visible={isFollowersLoading || isJobsLoading || isActionLoading} />

      {/* ── Tab pills ─────────────────────────────────────────────── */}
      <View className="flex-row justify-center gap-8 py-3 border-b border-gray-100">
        {TAB_LABELS.map((label, i) => (
          <TabPill
            key={label}
            label={label}
            active={activePage === i}
            onPress={() => goToPage(i)}
          />
        ))}
      </View>

      {/* ── ViewPager ─────────────────────────────────────────────── */}
      <PagerView
        ref={pagerRef}
        style={{ flex: 1 }}
        initialPage={0}
        onPageSelected={(e) => setActivePage(e.nativeEvent.position)}
      >
        {/* Page 0 — Followers grid */}
        <View key="followers" style={{ flex: 1 }}>
          <ScrollView
            contentContainerStyle={{
              flexDirection: 'row',
              flexWrap: 'wrap',
              paddingHorizontal: 8,
              paddingBottom: 32,
              paddingTop: 8,
            }}
          >
            {followerList.length === 0 && !isFollowersLoading && (
              <View className="flex-1 items-center mt-10">
                <Text className="text-gray-400">No followers registered at this location.</Text>
              </View>
            )}
            {followerList.map((item) => (
              <View key={item.deviceId} style={{ width: '33.33%' }}>
                <FollowerCard
                  follower={item}
                  selectionMode={selectionMode}
                  onPress={() => handleFollowerTap(item)}
                  onDelete={
                    selectionMode ? undefined : () => handleDeletePress(item)
                  }
                />
              </View>
            ))}
          </ScrollView>
        </View>

        {/* Page 1 — Assigned jobs */}
        <View key="jobs" style={{ flex: 1 }}>
          <ScrollView
            contentContainerStyle={{
              paddingHorizontal: 8,
              paddingBottom: 32,
              paddingTop: 8,
            }}
          >
            <JobListHeader />
            {jobList.length === 0 && !isJobsLoading ? (
              <Text className="text-center text-gray-400 mt-10">No assigned jobs</Text>
            ) : (
              jobList.map((item) => (
                <JobListRow
                  key={item.jobId}
                  job={item}
                  onBundlePress={() => onJobPress?.(item)}
                />
              ))
            )}
          </ScrollView>
        </View>
      </PagerView>
    </AppSheetModal>
  );
}

// ── Tab pill ──────────────────────────────────────────────────────────────────

function TabPill({
  label,
  active,
  onPress,
}: {
  label: string;
  active: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} className="items-center gap-1.5" hitSlop={8}>
      <Text className={`text-sm font-semibold ${active ? 'text-primary' : 'text-gray-400'}`}>
        {label}
      </Text>
      <View
        className="h-1.5 rounded-full"
        style={{
          width: active ? 24 : 10,
          backgroundColor: active ? '#21226b' : '#d1d5db',
        }}
      />
    </Pressable>
  );
}
