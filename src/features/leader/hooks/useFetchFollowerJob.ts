import { useMutation } from '@tanstack/react-query';
import { fetchFollowerJob, fetchFollowerEndBitJob } from '@/lib/api';
import type { FetchFollowerJobResponse, FetchFollowerEndBitJobResponse } from '@/types/follower';


export type FollowerJobResult =
  | { type: 'NORMAL'; data: FetchFollowerJobResponse }
  | { type: 'END_BIT'; data: FetchFollowerEndBitJobResponse }
  | { type: 'NONE' };

export const useFetchFollowerJob = () =>
  useMutation<
    FollowerJobResult,
    Error,
    { deviceId: string; location: string }
  >({
    mutationFn: async ({ deviceId, location }) => {
      console.log('[useFetchFollowerJob] 📡 GET /jobs/{deviceId}', { deviceId, location });
      try {
        // Step 1 — try normal job
        const normalRes = await fetchFollowerJob(deviceId, location);
        const normalJob = normalRes.data;

        if (normalJob.jobId != null) {
          console.log('[useFetchFollowerJob] ✅ Normal job found — jobId:', normalJob.jobId);
          return { type: 'NORMAL', data: normalJob };
        }

        // Step 2 — jobId is null, fall back to end-bit
        console.log('[useFetchFollowerJob] ↩️  No normal job — checking end-bit...');
        const endBitRes = await fetchFollowerEndBitJob(deviceId, location);
        const endBitJob = endBitRes.data;

        if (endBitJob.endBitJobId != null) {
          console.log('[useFetchFollowerJob] ✅ End-bit job found — id:', endBitJob.endBitJobId);
          return { type: 'END_BIT', data: endBitJob };
        }

        // Step 3 — neither job exists
        console.log('[useFetchFollowerJob] ℹ️  No job assigned to this follower');
        return { type: 'NONE' };

      } catch (err: any) {
        console.error(
          '[useFetchFollowerJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
