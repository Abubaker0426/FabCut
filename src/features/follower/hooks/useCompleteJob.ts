import { useMutation } from '@tanstack/react-query';
import { completeJob } from '@/lib/api';

export const useCompleteJob = () =>
  useMutation<boolean, Error, { jobId: string }>({
    mutationFn: async ({ jobId }) => {
      console.log('[useCompleteJob] 📡 PUT /jobs/{jobId}', { jobId });
      try {
        const res = await completeJob(jobId);
        console.log('[useCompleteJob] ✅ Job completed:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useCompleteJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
