import { useMutation } from '@tanstack/react-query';
import { assignJobToFollower } from '@/lib/api';

export const useAssignJob = () =>
  useMutation< number,Error,{ deviceId: string; data: import('@/types/leader').AssignJobRequest }
  >({
    mutationFn: async ({ deviceId, data }) => {
      console.log('[useAssignJob] 📡 POST /jobs/{deviceId}', { deviceId, data });
      try {
        const res = await assignJobToFollower(deviceId, data);
        console.log('[useAssignJob] ✅ Job assigned — jobId:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useAssignJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
