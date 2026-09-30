import React from 'react';
import { ScrollView, Text, View } from 'react-native';

interface SizeRowProps {
  sizes: string[];
  quantities: (number | string)[];
  /** Optional second quantity row label */
  secondRowLabel?: string;
  secondQuantities?: (number | string)[];
}

export function SizeRow({ sizes, quantities, secondQuantities }: SizeRowProps) {
  return (
    <View className="border border-gray-200 rounded-lg p-2 mb-4">
      <ScrollView horizontal showsHorizontalScrollIndicator={false}>
        <View>
          {/* Size row */}
          <View className="flex-row">
            {sizes.map((s, i) => (
              <View key={i} className="w-14 items-center py-1">
                <Text className="text-xs font-bold text-primary uppercase">{s}</Text>
              </View>
            ))}
          </View>
          {/* Quantity row */}
          <View className="flex-row">
            {quantities.map((q, i) => (
              <View key={i} className="w-14 items-center py-1">
                <Text className="text-xs text-gray-800">{q}</Text>
              </View>
            ))}
          </View>
          {/* Optional second quantity row */}
          {secondQuantities && (
            <View className="flex-row">
              {secondQuantities.map((q, i) => (
                <View key={i} className="w-14 items-center py-1">
                  <Text className="text-xs text-gray-500">{q}</Text>
                </View>
              ))}
            </View>
          )}
        </View>
      </ScrollView>
    </View>
  );
}
