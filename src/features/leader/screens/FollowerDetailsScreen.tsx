import { useLocalSearchParams, useRouter } from 'expo-router';
import { ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { Button } from '@/components/ui/Button';
import { SizeRow } from '@/components/ui/SizeRow';
import type { FetchFollowerJobResponse, FetchFollowerEndBitJobResponse } from '@/types/follower';

// ─── Detail block shape ───────────────────────────────────────────────────────

interface DetailBlock {
  description: string;   // itemCode + itemDesc (normal) | itemCode + partName (end-bit)
  sizes:       string[];
  quantities:  number[];
}

// ─── Parse helpers — mirrors Java FollowerDetailsPresenter ────────────────────

/**
 * Mirrors: FollowerDetailsPresenter.parseDetails(FetchFollowerResponse)
 * One block per jobDetail — description = "itemCode, itemDesc"
 */
function parseNormalBlocks(response: FetchFollowerJobResponse): DetailBlock[] {
  return (response.jobDetails ?? []).map((detail) => ({
    description: `${detail.itemCode}, ${detail.itemDesc}`,
    sizes:       (detail.ratioDetails ?? []).map((r) => r.size),
    quantities:  (detail.ratioDetails ?? []).map((r) => Number(r.quantity)),
  }));
}

/**
 * Mirrors: FollowerDetailsPresenter.parseEndBitDetails(FetchFollowerEndBitJobResponse)
 * One block per jobDetail — description = "itemCode, partName"
 */
function parseEndBitBlocks(response: FetchFollowerEndBitJobResponse): DetailBlock[] {
  return (response.jobDetails ?? []).map((detail) => ({
    description: `${response.itemCode}, ${detail.partName}`,
    sizes:       (detail.ratioDetails ?? []).map((r) => r.size),
    quantities:  (detail.ratioDetails ?? []).map((r) => Number(r.quantity)),
  }));
}

// ─── Screen ───────────────────────────────────────────────────────────────────

export default function FollowerDetailsScreen() {
  const router = useRouter();

  // jobType and full response JSON passed from FollowersBottomSheet
  // Mirrors Java: Intent extras with serialized response objects
  const { jobType, responseJson } = useLocalSearchParams<{
    jobType?:      'NORMAL' | 'END_BIT';
    responseJson?: string;
  }>();

  const isEndBit = jobType === 'END_BIT';

  // Parse the response and build display blocks — no API call, mirrors Java local parse
  let blocks: DetailBlock[] = [];
  let headerInfo = { ocNumber: '', fitType: '', layLength: '' };

  if (responseJson) {
    try {
      if (isEndBit) {
        const res = JSON.parse(responseJson) as FetchFollowerEndBitJobResponse;
        blocks = parseEndBitBlocks(res);
        headerInfo = {
          ocNumber:  res.ocNumber ?? '',
          fitType:   '',
          layLength: '',
        };
      } else {
        const res = JSON.parse(responseJson) as FetchFollowerJobResponse;
        blocks = parseNormalBlocks(res);
        headerInfo = {
          ocNumber:  res.ocNumber  ?? '',
          fitType:   res.fitType   ?? '',
          layLength: res.layLength != null ? String(res.layLength) : '',
        };
      }
    } catch {
      // Malformed JSON — show empty screen
    }
  }

  return (
    // White background — mirrors activity_follower_details.xml
    <SafeAreaView className="flex-1 bg-white" edges={['top']}>

      <CustomHeader
        title={isEndBit ? 'End Bit Job Details' : 'Job Details'}
        onBack={() => router.back()}
        badge={isEndBit ? 'END BIT' : undefined}
      />

      <ScrollView contentContainerStyle={{ paddingBottom: 40 }}>

        {/* Job summary banner — OC / fitType / layLength */}
        {(headerInfo.ocNumber || headerInfo.fitType) && (
          <View className="mx-4 mt-3 mb-1 bg-gray-50 rounded-lg px-4 py-3 border border-gray-100">
            <Text className="text-xs text-gray-500 uppercase tracking-wider">
              {headerInfo.ocNumber}
              {headerInfo.fitType ? `  ·  ${headerInfo.fitType}` : ''}
              {headerInfo.layLength ? `  ·  Lay ${headerInfo.layLength}` : ''}
            </Text>
          </View>
        )}

        {/* details_container — one follower_deatil_item block per job detail */}
        {blocks.length === 0 ? (
          <View className="items-center mt-20">
            <Text className="text-gray-400">No job details available.</Text>
          </View>
        ) : (
          blocks.map((block, index) => (
            <DetailItem key={index} block={block} />
          ))
        )}

        {/* go_back button — centered, mirrors @OnClick(R.id.go_back) → finish() */}
        <View className="items-center mt-4">
          <Button
            title="Go Back"
            variant="outline"
            onPress={() => router.back()}
            className="px-10"
          />
        </View>

      </ScrollView>
    </SafeAreaView>
  );
}

// ─── follower_deatil_item.xml ─────────────────────────────────────────────────
// padding 16dp | description italic centered 13sp | size grid (rounded_bg)

function DetailItem({ block }: { block: DetailBlock }) {
  return (
    <View style={{ padding: 16 }}>

      {/* description — italic, centered, colorBlack, 13sp, marginBottom 24dp */}
      <Text
        className="text-center italic text-gray-900"
        style={{ fontSize: 13, marginBottom: 24 }}
      >
        {block.description}
      </Text>

      {/* size_container + size_quantity_container — rounded_bg, padding 8dp */}
      <View className="rounded-lg border border-gray-200 p-2">
        <SizeRow sizes={block.sizes} quantities={block.quantities} />
      </View>

    </View>
  );
}
