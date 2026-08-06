/**
 * JobListRow + JobListHeader
 * Mirrors: job_list_row_item.xml + job_list_header_item.xml
 *
 * Header (dark primary card): OC | LAY | FOLLOWER | (empty)
 * Row   (white card):         oc# | lay# | table# | "SPLIT BUNDLE" button
 *                              item description below divider
 *
 * In Java the adapter always inserts position 0 as the header.
 * We export both and let the FlatList use ListHeaderComponent for the header.
 */
import { Pressable, Text, View } from 'react-native';

import type { OcLay } from '@/types';

// ── Header row ────────────────────────────────────────────────────────────────

export function JobListHeader() {
  return (
    <View
      className="mx-2 my-1 rounded-xl overflow-hidden"
      style={{ backgroundColor: '#21226b', elevation: 4 }}
    >
      <View className="flex-row px-3 py-3" style={{ margin: 8 }}>
        <ColHeader label="OC" />
        <ColHeader label="LAY" />
        <ColHeader label="FOLLOWER" />
        <View className="flex-1" />
      </View>
    </View>
  );
}

function ColHeader({ label }: { label: string }) {
  return (
    <Text
      className="flex-1 text-center text-xs font-bold text-white uppercase"
      numberOfLines={1}
    >
      {label}
    </Text>
  );
}

// ── Data row ──────────────────────────────────────────────────────────────────

interface JobListRowProps {
  job: OcLay;
  onBundlePress?: () => void;
}

export function JobListRow({ job, onBundlePress }: JobListRowProps) {
  return (
    <View
      className="bg-white mx-2 my-1 rounded-xl overflow-hidden"
      style={{ elevation: 2, shadowColor: '#000', shadowOpacity: 0.05, shadowRadius: 4 }}
    >
      {/* Top row: OC# | Lay | Table | Split Bundle */}
      <View
        className="flex-row items-center"
        style={{ padding: 6, margin: 8 }}
      >
        <Text className="flex-1 text-center text-sm text-gray-700" numberOfLines={1}>
          {job.ocNo}
        </Text>
        <Text className="flex-1 text-center text-sm text-gray-700" numberOfLines={1}>
          {job.lay}
        </Text>
        <Text className="flex-1 text-center text-sm text-gray-700" numberOfLines={1}>
          {job.tableNum}
        </Text>
        {/* "SPLIT BUNDLE" — tappable, mirrors Bundling TextView onClick */}
        <Pressable className="flex-1 items-center" onPress={onBundlePress} hitSlop={8}>
          <Text
            className="text-center text-xs font-bold uppercase"
            style={{ color: '#21226b' }}
            numberOfLines={1}
          >
            Split Bundle
          </Text>
        </Pressable>
      </View>

      {/* Divider */}
      <View style={{ height: 1, backgroundColor: '#9e9e9e' }} />

      {/* Item description */}
      <Text
        className="text-xs font-semibold uppercase px-3 py-2"
        style={{ color: '#21226b' }}
      >
        {job.itemDescription}
      </Text>
    </View>
  );
}
