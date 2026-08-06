/**
 * ScannerScreen — Camera barcode scanner
 * Mirrors: ScannerActivity + activity_scanner.xml
 *
 * Uses expo-camera CameraView for barcode scanning.
 * Returns to previous screen after a successful scan.
 */
import { Ionicons } from '@expo/vector-icons';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

export default function ScannerScreen() {
  const router = useRouter();
  const [permission, requestPermission] = useCameraPermissions();
  const [scanned, setScanned] = useState(false);

  // Permission not yet determined
  if (!permission) {
    return (
      <SafeAreaView className="flex-1 bg-black items-center justify-center">
        <Text className="text-white">Requesting camera permission...</Text>
      </SafeAreaView>
    );
  }

  // Permission denied
  if (!permission.granted) {
    return (
      <SafeAreaView className="flex-1 bg-black items-center justify-center px-8">
        <Ionicons name="camera-outline" size={64} color="#fff" />
        <Text className="text-white text-center mt-4 text-base">
          Camera access is required to scan barcodes.
        </Text>
        <Pressable
          onPress={requestPermission}
          className="mt-6 px-6 py-3 bg-primary rounded-xl"
        >
          <Text className="text-white font-semibold">Grant Permission</Text>
        </Pressable>
        <Pressable
          onPress={() => router.back()}
          className="mt-3 px-6 py-3 border border-white rounded-xl"
        >
          <Text className="text-white">Go Back</Text>
        </Pressable>
      </SafeAreaView>
    );
  }

  const handleBarCodeScanned = ({ data }: { data: string }) => {
    if (scanned) return;
    setScanned(true);
    // TODO: pass scanned barcode back via a shared store or router params
    console.log('Scanned barcode:', data);
    router.back();
  };

  return (
    <View className="flex-1 bg-black">
      <CameraView
        style={{ flex: 1 }}
        facing="back"
        barcodeScannerSettings={{
          barcodeTypes: ['qr', 'code128', 'code39', 'ean13', 'ean8'],
        }}
        onBarcodeScanned={scanned ? undefined : handleBarCodeScanned}
      />

      {/* Framing rect overlay */}
      <View className="absolute inset-0 items-center justify-center pointer-events-none">
        <View className="w-72 h-44 border-2 border-white rounded-xl opacity-70" />
        <Text className="text-white text-sm mt-4 opacity-80">
          Align barcode within the frame
        </Text>
      </View>

      {/* Back button */}
      <SafeAreaView className="absolute top-0 left-0 right-0" edges={['top']}>
        <Pressable
          onPress={() => router.back()}
          className="m-4 w-10 h-10 bg-black/50 rounded-full items-center justify-center"
        >
          <Ionicons name="arrow-back" size={22} color="#fff" />
        </Pressable>
      </SafeAreaView>

      {/* Re-scan button */}
      {scanned && (
        <View className="absolute bottom-12 left-0 right-0 items-center">
          <Pressable
            onPress={() => setScanned(false)}
            className="px-6 py-3 bg-white rounded-full"
          >
            <Text className="text-primary font-semibold">Tap to scan again</Text>
          </Pressable>
        </View>
      )}
    </View>
  );
}
