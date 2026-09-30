import { useQuery } from '@tanstack/react-query';
import { validateLocation } from '@/lib/api';

/**
 * Calls GET /service/validate/locations with lat/lng as query params.
 * The Java backend returns a plain List<String> of location name strings.
 * Mirrors: MainPresenter#doValidateLocationCall
 */
export const useLocations = (coords: { latitude: number; longitude: number } | null) =>
  useQuery<string[]>({
    queryKey: ['locations', coords],
    queryFn: async () => {
      console.log('[useLocations] 📡 Calling GET /service/validate/locations with params:', coords);
      try {
        const res = await validateLocation(coords!);
        console.log('[useLocations] ✅ Response status:', res.status);
        console.log('[useLocations] ✅ Response data:', JSON.stringify(res.data, null, 2));
        return res.data; // string[]
      } catch (err: any) {
        console.error(
          '[useLocations] ❌ API call failed:',
          err?.response?.status ?? 'No response',
          err?.response?.data ?? err?.message,
          'code:', err?.code,
          err,
        );
        throw err;
      }
    },
    enabled: !!coords,
    retry: false, // don't retry — network errors will mask the real problem
  });
