import { useQuery } from '@tanstack/react-query';
import { getMyJob, getMyEndBitJob } from '@/lib/api';
import type { FetchFollowerJobResponse, FetchFollowerEndBitJobResponse } from '@/types/follower';

export type MyJobResult =
  | { type: 'NORMAL';  data: FetchFollowerJobResponse }
  | { type: 'END_BIT'; data: FetchFollowerEndBitJobResponse }
  | { type: 'NONE' };

export const useGetMyJob = (
  deviceId: string,
  location: string | null,
  enabled: boolean = true,
) =>
  useQuery<MyJobResult>({
    queryKey: ['myJob', deviceId, location],
    queryFn: async () => {
      console.log('[useGetMyJob] 📡 GET /jobs/{deviceId}', { deviceId, location });
      try {
        // Step 1 — try normal job
        const normalRes = await getMyJob(deviceId, location!);
        const normalJob = normalRes.data;

        if (normalJob.jobId != null) {
          console.log('[useGetMyJob] ✅ Normal job found — jobId:', normalJob.jobId);
          return { type: 'NORMAL', data: normalJob };
        }

        // Step 2 — jobId is null, fall back to end-bit job
        console.log('[useGetMyJob] ↩️  No normal job — checking end-bit...');
        const endBitRes = await getMyEndBitJob(deviceId, location!);
        const endBitJob = endBitRes.data;

        if (endBitJob.endBitJobId != null) {
          console.log('[useGetMyJob] ✅ End-bit job found — id:', endBitJob.endBitJobId);
          return { type: 'END_BIT', data: endBitJob };
        }

        // Step 3 — no job at all
        console.log('[useGetMyJob] ℹ️  No job assigned to this device');
        return { type: 'NONE' };

      } catch (err: any) {
        console.error(
          '[useGetMyJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!deviceId && !!location && enabled,
    staleTime: 0,   // always re-fetch on refresh
    retry: false,
  });
