import { useState, useEffect } from 'react';
import * as ExpoLocation from 'expo-location';

export type GpsState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'granted'; coords: { latitude: number; longitude: number } }
  | { status: 'denied' }
  | { status: 'error'; message: string };

/**
 * Requests foreground location permission and fetches GPS coords on mount.
 * Mirrors: MainActivity#startLocationUpdates → mLocationCallback
 */
export const useGpsCoords = () => {
  const [state, setState] = useState<GpsState>({ status: 'idle' });

  useEffect(() => {
    (async () => {
      setState({ status: 'loading' });
      console.log('[useGpsCoords] 🚀 Requesting location permission...');

      const { status } = await ExpoLocation.requestForegroundPermissionsAsync();
      console.log('[useGpsCoords] 📍 Permission status:', status);

      if (status !== 'granted') {
        console.warn('[useGpsCoords] ❌ Permission denied');
        setState({ status: 'denied' });
        return;
      }

      try {
        const location = await ExpoLocation.getCurrentPositionAsync({
          accuracy: ExpoLocation.Accuracy.High,
        });
        const coords = {
          latitude: location.coords.latitude,
          longitude: location.coords.longitude,
        };
        console.log('[useGpsCoords] ✅ Coords fetched:', coords);
        setState({ status: 'granted', coords });
      } catch (e: any) {
        console.error('[useGpsCoords] ❌ GPS error:', e?.message);
        setState({ status: 'error', message: e?.message ?? 'Could not get location' });
      }
    })();
  }, []);

  return state;
};
