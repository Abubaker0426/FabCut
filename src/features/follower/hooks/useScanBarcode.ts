import { useMutation } from '@tanstack/react-query';
import { scanBarcode } from '@/lib/api';
import type { ScanBarcodeResponse } from '@/types/follower';

export const useScanBarcode = () =>
  useMutation<
    ScanBarcodeResponse,
    Error,
    { jobId: string; barcode: string; itemCode: string }
  >({
    mutationFn: async ({ jobId, barcode, itemCode }) => {
      console.log('[useScanBarcode] 📡 POST /jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode, itemCode,
      });
      try {
        const res = await scanBarcode(jobId, barcode, { itemCode });
        console.log('[useScanBarcode] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useScanBarcode] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
