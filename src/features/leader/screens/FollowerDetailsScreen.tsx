/**
 * FollowerDetailsScreen
 * Mirrors: FollowerDetailsActivity + activity_follower_details.xml
 *
 * Background: white (activity has no colorLightBlue — just plain white)
 * One follower_deatil_item.xml block per job detail.
 */
import { useLocalSearchParams, useRouter } from 'expo-router';
import { ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { Button } from '@/components/ui/Button';
import { SizeRow } from '@/components/ui/SizeRow';

interface DetailBlock {
  description: string;
  sizes: string[];
  quantities: number[];
}

// ─── Mock data ────────────────────────────────────────────────────────────────

const MOCK_NORMAL_BLOCKS: DetailBlock[] = [
  {
    description: 'IC-001 - Men Shirt - Blue',
    sizes:       ['S', 'M', 'L', 'XL'],
    quantities:  [20,  40,  30,  10],
  },
  {
    description: 'IC-002 - Men Shirt - White',
    sizes:       ['S', 'M', 'L'],
    quantities:  [15,  30,  25],
  },
];

const MOCK_ENDBIT_BLOCKS: DetailBlock[] = [
  {
    description: 'IC-001 - Front Panel',
    sizes:       ['S', 'M'],
    quantities:  [10,  15],
  },
  {
    description: 'IC-001 - Back Panel',
    sizes:       ['S', 'M'],
    quantities:  [10,  15],
  },
];

// ─── Screen ───────────────────────────────────────────────────────────────────

export default function FollowerDetailsScreen() {
  const router = useRouter();
  const { deviceId, jobType } = useLocalSearchParams<{
    deviceId?: string;
    jobType?: 'NORMAL' | 'END_BIT';
  }>();

  // TODO: fetch from API using deviceId
  const isEndBit = jobType === 'END_BIT';
  const blocks   = isEndBit ? MOCK_ENDBIT_BLOCKS : MOCK_NORMAL_BLOCKS;

  return (
    // White background — mirrors activity_follower_details.xml (no colorLightBlue)
    <SafeAreaView className="flex-1 bg-white" edges={['top']}>

      <CustomHeader
        title={isEndBit ? 'End Bit Job Details' : 'Job Details'}
        onBack={() => router.back()}
        badge={isEndBit ? 'END BIT' : undefined}
      />

      <ScrollView contentContainerStyle={{ paddingBottom: 40 }}>

        {/* details_container — one follower_deatil_item block per job detail */}
        {blocks.map((block, index) => (
          <DetailItem key={index} block={block} />
        ))}

        {/* go_back button — centered, mirrors @OnClick(R.id.go_back) */}
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
// padding: 16dp | description italic centered | size grid (rounded_bg)

function DetailItem({ block }: { block: DetailBlock }) {
  return (
    <View style={{ padding: 16 }}>

      {/* description — italic, centered, colorBlack, 13sp, marginBottom 24dp */}
      <Text
        className="text-center italic text-gray-900"
        style={{ fontSize: 13, marginBottom: 24, textAlign: 'center' }}
      >
        {block.description}
      </Text>

      {/* size_container + size_quantity_container — rounded_bg, padding 8dp */}
      <View
        className="rounded-lg border border-gray-200 p-2"
      >
        <SizeRow sizes={block.sizes} quantities={block.quantities} />
      </View>

    </View>
  );
}
