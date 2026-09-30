import { useMutation } from '@tanstack/react-query';
import { registerDevice } from '@/lib/api';
import type { RegisterRequest } from '@/types/auth';

/**
 * Calls POST /fabcut/cutting/registrations/{deviceId}
 * Body: { location, role, tableNumber }
 * Mirrors: MainPresenter#register
 */
export const useRegisterDevice = () =>
  useMutation<boolean, Error, { deviceId: string; data: RegisterRequest }>({
    mutationFn: async ({ deviceId, data }) => {
      // Server expects Title Case: "Leader" | "Follower"  (not "LEADER" / "FOLLOWER")
      const payload = {
        ...data,
        role: data.role.charAt(0) + data.role.slice(1).toLowerCase(), // "LEADER" → "Leader"
      };
      console.log('[useRegisterDevice] 📡 POST /registrations/{deviceId}', { deviceId, payload });
      try {
        const res = await registerDevice(deviceId, payload as any);
        console.log('[useRegisterDevice] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useRegisterDevice] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
