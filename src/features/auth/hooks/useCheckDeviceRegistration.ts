import { useQuery } from '@tanstack/react-query';
import { checkDeviceRegistration } from '@/lib/api';
import type { IsDevRegResponse } from '@/types/auth';

/**
 * Calls GET /fabcut/cutting/registrations/{deviceId}?location=...
 * Returns { devRegStatus: boolean, role: string }
 * Mirrors: MainPresenter#doIsDeviceRegisteredCall
 */
export const useCheckDeviceRegistration = (
  deviceId: string | null,
  location: string | null,
) =>
  useQuery<IsDevRegResponse>({
    queryKey: ['deviceRegistration', deviceId, location],
    queryFn: async () => {
      console.log(
        '[useCheckDeviceRegistration] 📡 GET /registrations/{deviceId}?location=',
        { deviceId, location },
      );
      try {
        const res = await checkDeviceRegistration(deviceId!, location!);
        console.log('[useCheckDeviceRegistration] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useCheckDeviceRegistration] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!deviceId && !!location,
    retry: false,
  });
