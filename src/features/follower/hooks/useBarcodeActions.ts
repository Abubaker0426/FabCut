import { useMutation } from '@tanstack/react-query';
import { validateActualPlies, removeBarcode } from '@/lib/api';
import type { ValidateActualPliesRequest } from '@/types/follower';

// ─── Validate actual plies ────────────────────────────────────────────────────

export const useValidatePlies = () =>
  useMutation<
    boolean,
    Error,
    { jobId: string; barcode: string; data: ValidateActualPliesRequest }
  >({
    mutationFn: async ({ jobId, barcode, data }) => {
      console.log('[useValidatePlies] 📡 PUT /jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode, data,
      });
      try {
        const res = await validateActualPlies(jobId, barcode, data);
        console.log('[useValidatePlies] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useValidatePlies] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });

// ─── Delete barcode ───────────────────────────────────────────────────────────

export const useDeleteBarcode = () =>
  useMutation<
    boolean,
    Error,
    { jobId: string; barcode: string }
  >({
    mutationFn: async ({ jobId, barcode }) => {
      console.log('[useDeleteBarcode] 📡 DELETE /jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode,
      });
      try {
        const res = await removeBarcode(jobId, barcode);
        console.log('[useDeleteBarcode] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useDeleteBarcode] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
