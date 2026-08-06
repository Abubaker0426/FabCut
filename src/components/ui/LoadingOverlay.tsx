import React from 'react';
import { ActivityIndicator, Modal, Text, View } from 'react-native';

interface LoadingOverlayProps {
  visible: boolean;
  message?: string;
}

export function LoadingOverlay({ visible, message = 'Loading...' }: LoadingOverlayProps) {
  return (
    <Modal transparent animationType="fade" visible={visible}>
      <View className="flex-1 bg-black/40 items-center justify-center">
        <View className="bg-white rounded-2xl px-10 py-8 items-center shadow-lg">
          <ActivityIndicator size="large" color="#21226b" />
          <Text className="mt-4 text-base text-gray-700">{message}</Text>
        </View>
      </View>
    </Modal>
  );
}
