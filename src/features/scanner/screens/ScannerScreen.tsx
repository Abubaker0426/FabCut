import { Ionicons } from '@expo/vector-icons';
import { useAudioPlayer, AudioModule } from 'expo-audio';
import { CameraView, useCameraPermissions, type BarcodeScanningResult } from 'expo-camera';
import { useCallback, useEffect, useRef, useState } from 'react';
import { Dimensions, Pressable, StyleSheet, Text, Vibration, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
const BEEP_SOUND = require('@/../assets/sound/beep.mp3');

const ROI_WIDTH = 290;
const ROI_HEIGHT = 290;
const ROI_TOLERANCE = 0.15; // fraction of ROI size a barcode's center may fall outside and still count

export interface ScannerScreenProps {
  onScanned: (code: string) => void;
  onClose: () => void;
  hint?: string;
}

export default function ScannerScreen({
  onScanned,
  onClose,
  hint = 'Align barcode within the frame',
}: ScannerScreenProps) {
  const [permission, requestPermission] = useCameraPermissions();
  const [scanned, setScanned] = useState(false);

  const roiRef = useRef<{ x: number; y: number; width: number; height: number } | null>(null);
  const codeReturnedRef = useRef(false);
  const player = useAudioPlayer(BEEP_SOUND);

  // Activate audio session on mount — required on Android to enable playback
  useEffect(() => {
    AudioModule.setAudioModeAsync({
      playsInSilentMode: true,
      shouldPlayInBackground: false,
    }).catch(() => {});
  }, []);

  const handleBarcodeScanned = useCallback(
    (result: BarcodeScanningResult) => {
      if (codeReturnedRef.current) return; // duplicate-scan guard

      const roi = roiRef.current;
      if (roi && result.bounds) {
        const { origin, size } = result.bounds;
        const centerX = origin.x + size.width / 2;
        const centerY = origin.y + size.height / 2;
        const marginX = roi.width * ROI_TOLERANCE;
        const marginY = roi.height * ROI_TOLERANCE;
        const withinX = centerX >= roi.x - marginX && centerX <= roi.x + roi.width + marginX;
        const withinY = centerY >= roi.y - marginY && centerY <= roi.y + roi.height + marginY;
        if (!withinX || !withinY) return; // outside the ROI — ignore
      }
      codeReturnedRef.current = true;
      setScanned(true);
      try {
        player.seekTo(0);
        player.play();
      } catch {
        // best-effort — never let a sound failure block returning the code
      }
      Vibration.vibrate(200);
      // Play sound first, then return the code after a short delay
      // so the player isn't released mid-playback by navigation
      setTimeout(() => { onScanned(result.data); }, 150);
    },
    [onScanned, player]
  );
  if (!permission) {
    return (
      <SafeAreaView className="flex-1 bg-black items-center justify-center">
        <Text className="text-white">Requesting camera permission...</Text>
      </SafeAreaView>
    );
  }
  if (!permission.granted) {
    return (
      <SafeAreaView className="flex-1 bg-black items-center justify-center px-8">
        <Ionicons name="camera-outline" size={64} color="#fff" />
        <Text className="text-white text-center mt-4 text-base">
          Camera access is required to scan barcodes.
        </Text>
        <Pressable onPress={requestPermission} className="mt-6 px-6 py-3 bg-primary rounded-xl">
          <Text className="text-white font-semibold">Grant Permission</Text>
        </Pressable>
        <Pressable onPress={onClose} className="mt-3 px-6 py-3 border border-white rounded-xl">
          <Text className="text-white">Go Back</Text>
        </Pressable>
      </SafeAreaView>
    );
  }
  return (
    <View className="flex-1 bg-black">
      <CameraView
        style={{ flex: 1 }}
        facing="back"
        barcodeScannerSettings={{ barcodeTypes: ['qr', 'code128', 'code39', 'ean13', 'ean8'] }}
        onBarcodeScanned={scanned ? undefined : handleBarcodeScanned}
      />
      {/* Dimmed mask with a clear ROI cut-out */}
      <View style={StyleSheet.absoluteFill} pointerEvents="none">
        <View style={styles.maskFill} />
        <View style={{ height: ROI_HEIGHT, flexDirection: 'row' }}>
          <View style={styles.maskFill} />
          <View
            style={styles.roiBox}
            onLayout={() => {
              const screen = Dimensions.get('window');
              roiRef.current = {
                x: (screen.width - ROI_WIDTH) / 2,
                y: (screen.height - ROI_HEIGHT) / 2,
                width: ROI_WIDTH,
                height: ROI_HEIGHT,
              };
            }}
          />
          <View style={styles.maskFill} />
        </View>
        <View style={styles.maskFill} />
      </View>
      <View className="absolute inset-0 items-center justify-center" pointerEvents="none">
        <Text className="text-white text-sm mt-52 opacity-80">{hint}</Text>
      </View>
      {/* Back button */}
      <SafeAreaView className="absolute top-0 left-0 right-0" edges={['top']}>
        <Pressable
          onPress={onClose}
          className="m-4 w-10 h-10 bg-black/50 rounded-full items-center justify-center"
        >
          <Ionicons name="arrow-back" size={22} color="#fff" />
        </Pressable>
      </SafeAreaView>
    </View>
  );
}

const styles = StyleSheet.create({
  maskFill: {
    flex: 1,
    backgroundColor: 'rgba(66,66,66,0.6)',
  },
  roiBox: {
    width: ROI_WIDTH,
    height: ROI_HEIGHT,
    borderWidth: 2,
    borderColor: '#fff',
    backgroundColor: 'transparent',
  },
});
