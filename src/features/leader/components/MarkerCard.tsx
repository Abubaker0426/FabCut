/**
 * MarkerCard — mirrors marker_item.xml
 */
import React from 'react';
import { Pressable, Text, View } from 'react-native';

import type { Marker } from '@/types';

interface MarkerCardProps {
  marker: Marker;
  onPress: () => void;
}

export function MarkerCard({ marker, onPress }: MarkerCardProps) {
  return (
    <Pressable
      onPress={onPress}
      className="active:opacity-70"
    >
      <View
        className="bg-white rounded-xl mx-2 my-2 p-4"
        style={{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.07, shadowRadius: 6 }}
      >
        <Row label="OC Number" value={marker.ocNumber ?? '-'} />
        <Row
          label="Item Code"
          value={marker.items.map((i) => i.itemCode).join(', ')}
        />
        <Row
          label="Item Description"
          value={marker.items.map((i) => i.itemDesc).join(' / ')}
        />
        <Row label="Shrinkage" value={`${marker.shrinkage}%`} />
      </View>
    </Pressable>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View className="flex-row mb-1">
      <Text className="flex-1 font-bold text-sm text-gray-800">{label}</Text>
      <Text className="flex-1 text-sm text-gray-600" numberOfLines={1} ellipsizeMode="tail">
        {value}
      </Text>
    </View>
  );
}
