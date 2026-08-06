/**
 * LocationPickerModal — mirrors dialog_location_picker.xml
 * Shown when GPS returns multiple valid factory locations.
 * User picks one, then registration continues.
 */
import React from 'react';
import { FlatList, Modal, Pressable, Text, View, } from 'react-native';
import type { Location } from '@/types';

interface LocationPickerModalProps {
  visible: boolean;
  locations: Location[];
  onSelect: (location: Location) => void;
  /** Pass an empty function to make the modal non-dismissable */
  onDismiss: () => void;
}

export function LocationPickerModal({ visible, locations, onSelect, onDismiss, }: LocationPickerModalProps) {
  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onDismiss}
    >
      <Pressable className="flex-1 bg-black/40 justify-center px-6">
        <Pressable onPress={(e) => e.stopPropagation()}>
          <View
            className="bg-white rounded-2xl overflow-hidden"
            style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12 }}
          >
            {/* Header */}
            <View className="px-5 py-4 border-b border-gray-100">
              <Text className="text-center font-bold text-primary">Select Location</Text>
            </View>

            {/* Location list */}
            <FlatList
              data={locations}
              keyExtractor={(item) => item.locationId}
              style={{ maxHeight: 320 }}
              renderItem={({ item }) => (
                <Pressable
                  onPress={() => onSelect(item)}
                  className="flex-row items-center px-5 py-4 border-b border-gray-50 active:bg-lightBlue"
                >
                  <View className="flex-1">
                    <Text className="text-lg font-semibold text-gray-800 pl-2">
                      {item.locationName}
                    </Text>
                  </View>
                </Pressable>
              )}
              ListEmptyComponent={
                <Text className="text-center text-gray-400 py-8">No locations found</Text>
              }
            />

            {/* No cancel — user must pick a location to proceed */}
          </View>
        </Pressable>
      </Pressable>
    </Modal>
  );
}
