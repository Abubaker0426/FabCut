import { useEffect } from 'react';
import { getDeviceId } from '@/lib/deviceId';
import { useAppStore } from '@/store/appStore';

/**
 * useDeviceId
 *
 * Loads the device ID once on mount via expo-application and stores it
 * in Zustand so every screen can read it without calling getDeviceId() again.
 *
 * Used in: MainScreen, LeaderScreen, FollowerScreen
 * Replaces the duplicated getDeviceId() + setDeviceId pattern in each screen.
 */
export const useDeviceId = () => {
  useEffect(() => {
    const stored = useAppStore.getState().deviceId;
    if (stored) return; // already loaded — skip

    getDeviceId().then((id) => {
      if (id) {
        console.log('[useDeviceId] 📱 Device ID loaded and stored:', id);
        useAppStore.getState().setDeviceId(id);
      } else {
        console.warn('[useDeviceId] ⚠️ Could not retrieve device ID');
      }
    });
  }, []);

  // Return from store so caller can use it reactively
  return useAppStore((s) => s.deviceId);
};
