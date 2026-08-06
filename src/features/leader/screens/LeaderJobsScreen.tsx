/**
 * LeaderJobsScreen
 * Mirrors: LeaderJobsActivity + activity_leader_jobs.xml
 *
 * - Ratio rows (job_row_item.xml): ratio row + quantity row, no label
 * - Tap ratio → EditJobModal → after submit → sheet opens in follower selection mode
 * - End Bits button: only visible when marker has exactly 1 item (Java condition)
 * - Bottom sheet: followers grid + assigned jobs tabs
 */
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Pressable, RefreshControl, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { SizeRow } from '@/components/ui/SizeRow';
import type { MarkerItem, OcLay, Ratio } from '@/types';
import { EditBundleModal } from '../components/EditBundleModal';
import { EditEndBitJobModal } from '../components/EditEndBitJobModal';
import { EditJobModal } from '../components/EditJobModal';
import { FollowersBottomSheet } from '../components/FollowersBottomSheet';
import { MOCK_LAY_NUMBERS, MOCK_RATIOS } from '../constants/leaderMockData';

function groupRatios(ratios: Ratio[]): Record<number, Ratio[]> {
  return ratios.reduce<Record<number, Ratio[]>>((acc, r) => {
    acc[r.ratioNumber] = [...(acc[r.ratioNumber] ?? []), r];
    return acc;
  }, {});
}

export default function LeaderJobsScreen() {
  const router = useRouter();
  const { markerJson } = useLocalSearchParams<{ markerJson?: string }>();
  const marker: MarkerItem | null = markerJson ? JSON.parse(markerJson) : null;

  const groups       = groupRatios(MOCK_RATIOS);
  const ratioNumbers = Object.keys(groups).map(Number);

  // End Bits button only visible when marker has exactly 1 item — Java condition:
  // if(markerItem.getItems().size() == 1) end_bit_button.setVisibility(View.VISIBLE)
  const showEndBits = (marker?.items?.length ?? 0) === 1;

  const [refreshing, setRefreshing]         = useState(false);
  const [loading]                           = useState(false);
  const [sheetOpen, setSheetOpen]           = useState(false);
  const [selectionMode, setSelectionMode]   = useState(false);
  const [editJobVisible, setEditJobVisible] = useState(false);
  const [endBitVisible, setEndBitVisible]   = useState(false);
  const [bundleVisible, setBundleVisible]   = useState(false);
  const [selectedRatio, setSelectedRatio]   = useState<number | null>(null);
  const [selectedJob, setSelectedJob]       = useState<OcLay | null>(null);

  const onRefresh = () => {
    setRefreshing(true);
    // TODO: GET /ratios/{markerUnique}
    setTimeout(() => setRefreshing(false), 800);
  };

  // After assign job submitted → open bottom sheet in follower selection mode
  const handleJobAssigned = () => {
    setEditJobVisible(false);
    setSelectionMode(true);   // IDLE=green, BUSY=disabled
    setSheetOpen(true);
  };

  const handleEndBitAssigned = () => {
    setEndBitVisible(false);
    setSelectionMode(true);
    setSheetOpen(true);
  };

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={loading} />

      <CustomHeader
        title={marker?.ocNumber ?? 'Jobs'}
        onBack={() => router.back()}
        rightActions={[
          { icon: 'people-outline', onPress: () => setSheetOpen(true), label: 'Followers' },
        ]}
      />

      {/* Ratio list — paddingBottom 52dp to clear bottom sheet peek */}
      <FlatList
        data={ratioNumbers}
        keyExtractor={(n) => String(n)}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        contentContainerStyle={{ paddingBottom: 80 }}
        ListHeaderComponent={
          <Text
            className="text-center text-xl font-bold text-primary"
            style={{ margin: 12 }}
          >
            Select a job to assign it to a follower
          </Text>
        }
        ListFooterComponent={
          showEndBits ? (
            <Pressable
              onPress={() => setEndBitVisible(true)}
              className="mx-2.5 my-2 bg-gray-100 rounded-lg items-center justify-center"
              style={{ height: 48 }}
            >
              <Text className="text-gray-700 font-medium">End bits</Text>
            </Pressable>
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

      {/* Modals */}
      <EditJobModal
        visible={editJobVisible}
        ratioNumber={selectedRatio}
        ratios={selectedRatio !== null ? groups[selectedRatio] : []}
        onDismiss={() => setEditJobVisible(false)}
        onSubmit={handleJobAssigned}
      />

      <EditEndBitJobModal
        visible={endBitVisible}
        ratios={Object.values(groups)[0] ?? []}
        items={marker?.items ?? []}
        layNumbers={MOCK_LAY_NUMBERS}
        onDismiss={() => setEndBitVisible(false)}
        onSubmit={handleEndBitAssigned}
      />

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

      <FollowersBottomSheet
        visible={sheetOpen}
        selectionMode={selectionMode}
        onClose={() => { setSheetOpen(false); setSelectionMode(false); }}
        onFollowerPress={(follower) => {
          // Assign pending job to this follower, then reset selection mode
          setSheetOpen(false);
          setSelectionMode(false);
          console.log('Assign to follower:', follower.deviceId);
          // TODO: call assignJob / assignEndBitJob API
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

// ── Ratio card — mirrors job_row_item.xml exactly ─────────────────────────────
// Two dynamic rows: ratio row on top, quantity row below. NO label.

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
        {/* margin 8dp inner content — matches job_row_item.xml LinearLayout margin */}
        <View style={{ margin: 8 }}>
          <SizeRow sizes={sizes} quantities={ratios} secondQuantities={quantities} />
        </View>
      </View>
    </Pressable>
  );
}
