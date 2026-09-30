import { useRouter } from 'expo-router';
import ScannerScreen from '@/features/scanner/screens/ScannerScreen';
import { useAppStore } from '@/store/appStore';

export default function ScannerRoute() {
  const router = useRouter();

  return (
    <ScannerScreen
      onScanned={(code) => {
        console.log('[ScannerRoute] scanned:', code);
        // Store the code in Zustand so FollowerScreen can read it after back()
        useAppStore.getState().setLastScannedCode(code);
        router.back();
      }}
      onClose={() => router.back()}
    />
  );
}
