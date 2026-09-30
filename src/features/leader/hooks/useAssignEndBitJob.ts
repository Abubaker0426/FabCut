import { useMutation } from '@tanstack/react-query';
import { assignEndBitJobToFollower } from '@/lib/api';
import type { AssignEndBitJobRequest } from '@/types/leader';

export const useAssignEndBitJob = () =>
  useMutation<
    boolean,
    Error,
    { deviceId: string; data: AssignEndBitJobRequest }
  >({
    mutationFn: async ({ deviceId, data }) => {
      console.log('[useAssignEndBitJob] 📡 POST /end-bits/jobs/{deviceId}', { deviceId, data });
      try {
        const res = await assignEndBitJobToFollower(deviceId, data);
        console.log('[useAssignEndBitJob] ✅ End-bit job assigned:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useAssignEndBitJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
