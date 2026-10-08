import { Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { useUnregisterDevice } from './useUnregisterDevice';
import { useDeviceId } from './useDeviceId';
import { useAppStore } from '@/store/appStore';
import { toast } from '@/lib/toast';
import type { Role } from '@/types/common';

/**
 * useUnregister
 *
 * Full unregister flow — Alert confirm → API call → store reset → navigate to /.
 * Shared by LeaderScreen and RefreshScreen (Follower) via the header overflow menu.
 *
 * Handles:
 *   - Loading deviceId from device if not in store
 *   - Confirmation dialog (same in both leader and follower)
 *   - DELETE /registrations/{deviceId} with { location, role }
 *   - Store reset + navigate to /
 *
 * Usage:
 *   const { handleUnregister, isUnregistering } = useUnregister();
 */
export const useUnregister = () => {
  const router   = useRouter();
  const location = useAppStore((s) => s.location);
  const role     = useAppStore((s) => s.role);
  const deviceId = useDeviceId(); // loads + stores deviceId if missing

  const { mutate: unregister, isPending: isUnregistering } = useUnregisterDevice();

  const performUnregister = () => {
    console.log('[useUnregister] 🔍 performUnregister called');
    console.log('[useUnregister] deviceId:', deviceId);
    console.log('[useUnregister] location:', location);
    console.log('[useUnregister] role:', role);

    if (!deviceId || !location || !role) {
      console.warn('[useUnregister] ❌ Missing required fields');
      toast.error('Cannot unregister — device info missing. Try restarting the app.', 'Error');
      return;
    }

    console.log('[useUnregister] 📡 Calling DELETE /registrations/{deviceId}...');
    unregister(
      { deviceId, data: { location, role: role as Role } },
      {
        onSuccess: () => {
          console.log('[useUnregister] ✅ Unregistered — resetting store and navigating to /');
          useAppStore.getState().reset();
          router.replace('/');
        },
        onError: (err: any) => {
          console.error('[useUnregister] ❌ Failed:', err?.response?.status, err?.response?.data ?? err?.message);
          toast.apiError(err, 'Unregister Failed');
        },
      },
    );
  };

  const handleUnregister = () => {
    console.log('[useUnregister] 🔘 Unregister tapped — showing confirmation dialog');
    Alert.alert(
      'Unregister device',
      'Are you sure you want to unregister this device?',
      [
        { text: 'NO',  style: 'cancel' },
        { text: 'YES', style: 'destructive', onPress: performUnregister },
      ],
      { cancelable: true },
    );
  };

  return { handleUnregister, isUnregistering };
};
