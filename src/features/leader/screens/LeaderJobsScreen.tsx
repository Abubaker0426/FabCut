import { useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Pressable, RefreshControl, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { SizeRow } from '@/components/ui/SizeRow';
import { toast } from '@/lib/toast';
import { useAppStore } from '@/store/appStore';
import type { MarkerItem, OcLay, Ratio, AssignJobRequest, AssignEndBitJobRequest } from '@/types/leader';

import { EditBundleModal } from '../components/EditBundleModal';
import { EditEndBitJobModal } from '../components/EditEndBitJobModal';
import { EditJobModal } from '../components/EditJobModal';
import { FollowersBottomSheet } from '../components/FollowersBottomSheet';
import { useFetchJobDetails } from '../hooks/useFetchJobDetails';
import { useAssignJob } from '../hooks/useAssignJob';
import { useFetchLayNumbers } from '../hooks/useFetchLayNumbers';
import { useAssignEndBitJob } from '../hooks/useAssignEndBitJob';
import { useFetchPartsForOC } from '../hooks/useFetchPartsForOC';

function groupRatios(ratios: Ratio[]): Record<number, Ratio[]> {
  return ratios.reduce<Record<number, Ratio[]>>((acc, r) => {
    acc[r.ratioNumber] = [...(acc[r.ratioNumber] ?? []), r];
    return acc;
  }, {});
}

// ─── Screen ───────────────────────────────────────────────────────────────────

export default function LeaderJobsScreen() {
  const router = useRouter();
  const location = useAppStore((s) => s.location);

  // Read marker from Zustand store — stored before navigation to avoid
  // Expo Router corrupting special characters in markerUnique via URL params
  const marker = useAppStore((s) => s.selectedMarker);

  // End Bits button only visible when marker has exactly 1 item
  // Mirrors Java: if(markerItem.getItems().size() == 1) end_bit_button.setVisibility(View.VISIBLE)
  const showEndBits = (marker?.items?.length ?? 0) === 1;

  // ── Hook: fetch job details (ratios) ────────────────────────────────────────
  // Mirrors Java: LeaderJobsActivity#onCreate → mPresenter.fetchJobDetails(markerItem)
  const {
    data: jobDetails,
    isLoading,
    isError,
    refetch,
    isRefetching,
  } = useFetchJobDetails(marker?.markerUnique ?? '');

  // Group ratios by ratioNumber — each group = one RatioCard
  const ratios       = jobDetails?.ratios ?? [];
  const groups       = groupRatios(ratios);
  const ratioNumbers = Object.keys(groups).map(Number);

  // ── UI state ─────────────────────────────────────────────────────────────────
  const [sheetOpen, setSheetOpen]           = useState(false);
  const [selectionMode, setSelectionMode]   = useState(false);
  const [editJobVisible, setEditJobVisible] = useState(false);
  const [endBitVisible, setEndBitVisible]   = useState(false);
  const [bundleVisible, setBundleVisible]   = useState(false);
  const [selectedRatio, setSelectedRatio]   = useState<number | null>(null);
  const [selectedJob, setSelectedJob]       = useState<OcLay | null>(null);

  // Pending assign request — built in EditJobModal, consumed when follower is picked
  // Mirrors Java: request stored after prepareJobData(), used in LeaderActivity.submit()
  const [pendingRequest, setPendingRequest]             = useState<AssignJobRequest | null>(null);
  // Pending end-bit request — built in EditEndBitJobModal, same follower-pick pattern
  const [pendingEndBitRequest, setPendingEndBitRequest] = useState<AssignEndBitJobRequest | null>(null);

  // ── Hook: assign normal job to follower ──────────────────────────────────────
  const { mutate: assignJob,       isPending: isAssigning }       = useAssignJob();
  // ── Hook: assign end-bit job to follower ─────────────────────────────────────
  const { mutate: assignEndBitJob, isPending: isAssigningEndBit } = useAssignEndBitJob();

  // ── Hook: lay numbers for end-bit modal ──────────────────────────────────────
  // Fires only when endBitVisible=true and marker has exactly 1 item
  // Mirrors Java: EditEndBitJobDialog opens → mPresenter.fetchLayNumbers(markerId, itemCode, location)
  const endBitItemCode = marker?.items?.[0]?.itemCode ?? '';
  const {
    data: layNumbers,
    isLoading: isLayNumbersLoading,
  } = useFetchLayNumbers(
    marker?.markerUnique ?? '',
    endBitItemCode,
    location,
    endBitVisible,
  );

  // ── Hook: fetch parts for end-bit modal ──────────────────────────────────────
  // Mirrors Java: EditEndBitJobDialogPresenter.fetchPartNames(ocNo)
  //   → doFetchPartsForOCCall(ocNo, LocationRequest) → updatePartNamesList(parts)
  const {
    data: partsData,
    isLoading: isPartsLoading,
  } = useFetchPartsForOC(
    marker?.ocNumber ?? '',
    location,
    endBitVisible,
  );

  // ── Pull-to-refresh ──────────────────────────────────────────────────────────
  // Mirrors Java: SwipeRefreshLayout → mPresenter.fetchJobDetails()
  const onRefresh = () => { refetch(); };

  // ── After job assigned → open bottom sheet in follower selection mode ────────
  // Mirrors Java: after EditJobDialog.onSubmit → openFollowerSelectionSheet()
  // Stores the built AssignJobRequest — used when the leader picks a follower
  const handleJobAssigned = (builtRequest: AssignJobRequest) => {
    setEditJobVisible(false);
    setPendingRequest(builtRequest);
    setSelectionMode(true);   // IDLE=green (selectable), BUSY=greyed out
    setSheetOpen(true);
  };

  const handleEndBitAssigned = (builtRequest: AssignEndBitJobRequest) => {
    setEndBitVisible(false);
    setPendingEndBitRequest(builtRequest);
    setSelectionMode(true);
    setSheetOpen(true);
  };

  // ── Error state ───────────────────────────────────────────────────────────────
  if (isError) {
    toast.apiError(null, 'Error', 'Failed to load job details.');
  }

  // ── Render ────────────────────────────────────────────────────────────────────
  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={isLoading || isAssigning || isAssigningEndBit || isLayNumbersLoading || isPartsLoading} />

      <CustomHeader
        title={marker?.ocNumber ?? 'Jobs'}
        onBack={() => router.back()}
        rightActions={[
          { icon: 'people-outline', onPress: () => setSheetOpen(true), label: 'Followers' },
        ]}
      />

      <FlatList
        data={ratioNumbers}
        keyExtractor={(n) => String(n)}
        refreshControl={
          <RefreshControl refreshing={isRefetching} onRefresh={onRefresh} />
        }
        contentContainerStyle={{ paddingBottom: 80 }}
        ListHeaderComponent={
          <View className="mx-2 mt-3 mb-1">
            {/* Job info banner — custName / styleNo / itemDesc from API */}
            {jobDetails && (
              <View className="bg-white rounded-lg px-4 py-3 mb-2"
                style={{ elevation: 2, shadowColor: '#000', shadowOpacity: 0.06, shadowRadius: 4 }}>
                <Text className="text-xs text-gray-500 uppercase tracking-wider">
                  {jobDetails.custName}  ·  {jobDetails.styleNo}
                </Text>
                <Text className="text-sm font-semibold text-gray-800 mt-0.5">
                  {jobDetails.itemDesc}
                </Text>
              </View>
            )}
            <Text className="text-center text-base font-semibold text-primary my-2">
              Select a job to assign to a follower
            </Text>
          </View>
        }
        ListFooterComponent={
          showEndBits ? (
            <Pressable
              onPress={() => setEndBitVisible(true)}
              className="mx-2.5 mt-6 bg-primary rounded-lg items-center justify-center"
              style={{ height: 48 }}
            >
              <Text className="text-white font-medium">End Bits</Text>
            </Pressable>
          ) : null
        }
        ListEmptyComponent={
          !isLoading ? (
            <View className="items-center mt-20">
              <Text className="text-gray-400">No ratios found for this marker.</Text>
            </View>
          ) : null
        }
        renderItem={({ item: ratioNum }) => (
          <RatioCard
            rows={groups[ratioNum]}
            onPress={() => {
              setSelectedRatio(ratioNum);
              setEditJobVisible(true);
            }}
          />
        )}
      />

      {/* ── Modals ── */}

      {/* Assign normal job — tap a ratio card */}
      <EditJobModal
        visible={editJobVisible}
        ratioNumber={selectedRatio}
        ratios={selectedRatio !== null ? groups[selectedRatio] : []}
        items={marker?.items ?? []}
        onDismiss={() => setEditJobVisible(false)}
        onSubmit={(data) => {
          // Build AssignJobRequest from EditJobModal data
          // Mirrors Java: EditJobDialogPresenter.prepareJobData() → AssignJobRequest
          const request: AssignJobRequest = {
            markerUnique: marker?.markerUnique ?? '',
            location: location ?? '',
            layLength: parseFloat(data.items[0]?.layLength ?? '0') || 0,
            jobDetails: data.items.map((item) => ({
              itemCode: item.itemCode,
              itemDesc: marker?.items.find((i) => i.itemCode === item.itemCode)?.itemDesc ?? '',
              shade: item.shade,
              shrinkage: item.shrinkage,
              pattern: item.pattern,
              ratioDetails: Object.entries(item.quantities).map(([size, qty]) => ({
                size,
                quantity: parseInt(qty, 10) || 0,
                ratio: parseInt(data.ratios[size] ?? '0', 10) || 0,
              })),
            })),
          };
          handleJobAssigned(request);
        }}
      />

      {/* Assign end-bit job — tap End Bits button */}
      <EditEndBitJobModal
        visible={endBitVisible}
        ratios={ratioNumbers.length > 0 ? groups[ratioNumbers[0]] : []}
        items={marker?.items ?? []}
        layNumbers={(layNumbers ?? []).map(String)}
        parts={partsData?.parts ?? []}
        isPartsLoading={isPartsLoading}
        onDismiss={() => setEndBitVisible(false)}
        onSubmit={(data) => {
          // Build AssignEndBitJobRequest from modal data
          // Mirrors Java: EditEndBitJobDialogPresenter.prepareJobData()
          const itemDesc = marker?.items.find((i) => i.itemCode === data.itemCode)?.itemDesc ?? '';

          const request: AssignEndBitJobRequest = {
            markerId:   marker?.markerUnique ?? '',
            location:   location ?? '',
            layNumber:  parseInt(data.layNumber, 10) || 0,
            itemCode:   data.itemCode,
            itemDesc,
            // One jobDetail per selected part — mirrors Java selectedParts loop
            jobDetails: data.selectedParts.map((partName) => ({
              partName,
              layLength: 0,
              ratioDetails: (ratioNumbers.length > 0 ? groups[ratioNumbers[0]] : []).map((r) => ({
                size:     r.size,
                quantity: parseInt(data.quantities[`${partName}___${r.size}`] ?? '0', 10) || 0,
              })),
            })),
          };
          handleEndBitAssigned(request);
        }}
      />

      {/* Bundle split — tap a job in the Jobs tab */}
      <EditBundleModal
        visible={bundleVisible}
        ocNumber={selectedJob?.ocNo ?? ''}
        jobId={selectedJob?.jobId ?? ''}
        totalQuantity={100}
        onDismiss={() => { setBundleVisible(false); setSelectedJob(null); }}
        onSubmit={(rows) => {
          setBundleVisible(false);
          console.log('Bundle split:', rows);
        }}
      />

      {/* Followers bottom sheet — followers tab + jobs tab */}
      <FollowersBottomSheet
        visible={sheetOpen}
        selectionMode={selectionMode}
        onClose={() => { setSheetOpen(false); setSelectionMode(false); }}
        onFollowerPress={(follower) => {
          const hasNormal = !!pendingRequest;
          const hasEndBit = !!pendingEndBitRequest;
          if (!hasNormal && !hasEndBit) return;

          setSheetOpen(false);
          setSelectionMode(false);

          if (hasNormal) {
            // Mirrors Java: mPresenter.doAssignJobToFollowerCall(deviceId, request)
            assignJob(
              { deviceId: follower.deviceId, data: pendingRequest! },
              {
                onSuccess: (jobId) => {
                  setPendingRequest(null);
                  toast.success(`Job assigned. Job ID: ${jobId}`);
                },
                onError: (err: any) => {
                  toast.apiError(err, 'Assign Failed', 'Failed to assign job.');
                },
              },
            );
          } else {
            // Mirrors Java: mPresenter.doAssignEndBitJobToFollowerCall(deviceId, request)
            assignEndBitJob(
              { deviceId: follower.deviceId, data: pendingEndBitRequest! },
              {
                onSuccess: () => {
                  setPendingEndBitRequest(null);
                  toast.success('End-bit job assigned successfully.');
                },
                onError: (err: any) => {
                  toast.apiError(err, 'Assign Failed', 'Failed to assign end-bit job.');
                },
              },
            );
          }
        }}
        onJobPress={(job) => {
          setSelectedJob(job);
          setSheetOpen(false);
          setSelectionMode(false);
          setBundleVisible(true);
        }}
      />
    </SafeAreaView>
  );
}

// ── RatioCard — mirrors job_row_item.xml ──────────────────────────────────────
// Two dynamic rows: sizes on top, ratios + quantities below. No static label.

function RatioCard({ rows, onPress }: { rows: Ratio[]; onPress: () => void }) {
  const sizes      = rows.map((r) => r.size);
  const ratios     = rows.map((r) => String(r.ratio));
  const quantities = rows.map((r) => String(r.ratioQty));

  return (
    <Pressable onPress={onPress} className="active:opacity-70">
      <View
        className="bg-white mx-2 my-2"
        style={{
          borderRadius: 6,
          elevation: 4,
          shadowColor: '#000',
          shadowOpacity: 0.08,
          shadowRadius: 4,
          padding: 16,
        }}
      >
        <View style={{ margin: 8 }}>
          <SizeRow sizes={sizes} quantities={ratios} secondQuantities={quantities} />
        </View>
      </View>
    </Pressable>
  );
}
