import { useMutation } from '@tanstack/react-query';
import { deleteFollowerJob, deleteFollowerEndBitJob } from '@/lib/api';
import type { EndBitJobRequest } from '@/types/follower';

export const useDeleteFollowerJob = () =>
  useMutation<boolean, Error, { jobId: string }>({
    mutationFn: async ({ jobId }) => {
      console.log('[useDeleteFollowerJob] 📡 DELETE /jobs/{jobId}', { jobId });
      try {
        const res = await deleteFollowerJob(jobId);
        console.log('[useDeleteFollowerJob] ✅ Deleted:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useDeleteFollowerJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });

/**
 * useDeleteEndBitJob
 *
 * Deletes the end-bit job assigned to a follower.
 * Triggered when the leader long-presses a follower card, normal job is null,
 * falls through to end-bit job, and confirms the delete dialog.
 *
 * Mirrors Java: LeaderPresenter#deleteEndBitJob(endBitJobId, deviceId)
 *   → doDeleteFollowerEndBitJobCall(endBitJobId, request)
 *
 * DELETE /fabcut/cutting/end-bits/jobs/{endBitJobId}
 * Body: { deviceId, location }
 * Response: boolean
 */
export const useDeleteEndBitJob = () =>
  useMutation<boolean, Error, { endBitJobId: string; data: EndBitJobRequest }>({
    mutationFn: async ({ endBitJobId, data }) => {
      console.log('[useDeleteEndBitJob] 📡 DELETE /end-bits/jobs/{id}', { endBitJobId, data });
      try {
        const res = await deleteFollowerEndBitJob(endBitJobId, data);
        console.log('[useDeleteEndBitJob] ✅ Deleted:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useDeleteEndBitJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
