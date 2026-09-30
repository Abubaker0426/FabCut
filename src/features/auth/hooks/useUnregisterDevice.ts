import { useMutation } from '@tanstack/react-query';
import { unregisterDevice } from '@/lib/api';
import type { UnregisterRequest } from '@/types/auth';

/**
 * Calls DELETE /fabcut/cutting/registrations/{deviceId}
 * Body: { location, role }
 * Mirrors: MainPresenter#unregister
 */
export const useUnregisterDevice = () =>
  useMutation<boolean, Error, { deviceId: string; data: UnregisterRequest }>({
    mutationFn: async ({ deviceId, data }) => {
      // Server expects Title Case: "Leader" | "Follower"
      const payload = {
        ...data,
        role: data.role.charAt(0) + data.role.slice(1).toLowerCase(), // "LEADER" → "Leader"
      };
      console.log('[useUnregisterDevice] 📡 DELETE /registrations/{deviceId}', { deviceId, payload });
      try {
        const res = await unregisterDevice(deviceId, payload as any);
        console.log('[useUnregisterDevice] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useUnregisterDevice] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
