/**
 * FollowersBottomSheet
 * Mirrors: followers_jobs_bottom_sheet.xml + ViewPager behaviour
 *
 * Uses react-native-pager-view (direct equivalent of Android ViewPager).
 *
 * Two pages — swipe left/right OR tap the tab pills:
 *  Page 0 — Followers grid  (3 columns)
 *  Page 1 — Assigned Jobs list
 *
 * Tab indicator updates automatically when swiping.
 */
import BottomSheet from '@gorhom/bottom-sheet';
import { useEffect, useRef, useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import PagerView from 'react-native-pager-view';
import { useRouter } from 'expo-router';

import AppSheetModal, { BottomSheetFlatList } from '@/components/Common/AppSheetModal';
import type { Follower, OcLay } from '@/types';
import { MOCK_ASSIGNED_JOBS, MOCK_FOLLOWERS } from '../constants/leaderMockData';
import { FollowerCard } from './FollowerCard';
import { JobListHeader, JobListRow } from './JobListRow';

const TAB_LABELS = ['Followers', 'Jobs Assigned'] as const;

interface Props {
  visible: boolean;
  onClose: () => void;
  selectionMode?: boolean;
  onFollowerPress?: (f: Follower) => void;
  onJobPress?: (j: OcLay) => void;
}

export function FollowersBottomSheet({
  visible,
  onClose,
  selectionMode = false,
  onFollowerPress,
  onJobPress,
}: Props) {
  const router   = useRouter();
  const sheetRef = useRef<BottomSheet>(null);
  const pagerRef = useRef<PagerView>(null);
  const [activePage, setActivePage] = useState(0);

  useEffect(() => {
    if (visible) {
      sheetRef.current?.snapToIndex(1);
    } else {
      sheetRef.current?.close();
    }
  }, [visible]);

  // Tap pill → programmatically scroll ViewPager (mirrors showFollowers.setOnClickListener)
  const goToPage = (index: number) => {
    pagerRef.current?.setPage(index);
    setActivePage(index);
  };

  return (
    <AppSheetModal
      ref={sheetRef}
      title={TAB_LABELS[activePage]}
      badge={activePage === 0 ? MOCK_FOLLOWERS.length : MOCK_ASSIGNED_JOBS.length}
      snapPoints={['7%', '55%', '85%']}
      initialIndex={-1}
      onClose={onClose}
      scrollable={false}
    >
      {/* ── Tab pills ────────────────────────────────────────────── */}
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

      {/* ── ViewPager — swipe between pages ─────────────────────── */}
      <PagerView
        ref={pagerRef}
        style={{ flex: 1 }}
        initialPage={0}
        onPageSelected={(e) => setActivePage(e.nativeEvent.position)}
      >
        {/* Page 0 — Followers grid */}
        <View key="followers" style={{ flex: 1 }}>
          <BottomSheetFlatList
            data={MOCK_FOLLOWERS}
            numColumns={3}
            keyExtractor={(item) => item.deviceId}
            contentContainerStyle={{ paddingHorizontal: 8, paddingBottom: 32, paddingTop: 8 }}
            renderItem={({ item }) => (
              <View style={{ flex: 1 / 3 }}>
                <FollowerCard
                  follower={item}
                  selectionMode={selectionMode}
                  onPress={() => onFollowerPress?.(item)}
                  onDelete={
                    selectionMode
                      ? undefined
                      : () => {
                          sheetRef.current?.close();
                          router.push({
                            pathname: '/leader/follower-details',
                            params: { deviceId: item.deviceId },
                          });
                        }
                  }
                />
              </View>
            )}
          />
        </View>

        {/* Page 1 — Assigned jobs */}
        <View key="jobs" style={{ flex: 1 }}>
          <BottomSheetFlatList
            data={MOCK_ASSIGNED_JOBS}
            keyExtractor={(item) => item.jobId}
            contentContainerStyle={{ paddingHorizontal: 8, paddingBottom: 32, paddingTop: 8 }}
            ListHeaderComponent={<JobListHeader />}
            renderItem={({ item }) => (
              <JobListRow job={item} onBundlePress={() => onJobPress?.(item)} />
            )}
            ListEmptyComponent={
              <Text className="text-center text-gray-400 mt-10">No assigned jobs</Text>
            }
          />
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
