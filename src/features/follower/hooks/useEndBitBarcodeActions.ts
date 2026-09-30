import { useMutation } from '@tanstack/react-query';
import { scanEndBitBarcode, validateEndBitActualPlies, removeEndBitBarcode, completeEndBitJob } from '@/lib/api';
import type { EndBitJobRequest, ValidateActualPliesRequest, ScanBarcodeResponse } from '@/types/follower';

export const useEndBitScanBarcode = () =>
  useMutation<
    ScanBarcodeResponse,
    Error,
    { jobId: string; barcode: string; partName: string; data: EndBitJobRequest }
  >({
    mutationFn: async ({ jobId, barcode, partName, data }) => {
      console.log('[useEndBitScanBarcode] 📡 POST /end-bits/jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode, partName,
      });
      try {
        const res = await scanEndBitBarcode(jobId, barcode, partName, data);
        console.log('[useEndBitScanBarcode] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useEndBitScanBarcode] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
            
        );
        throw err;
      }
    },
  });







export const useEndBitValidatePlies = () =>
  useMutation<
    boolean,
    Error,
    { jobId: string; barcode: string; partName: string; data: ValidateActualPliesRequest & { deviceId: string; location: string } }
  >({
    mutationFn: async ({ jobId, barcode, partName, data }) => {
      console.log('[useEndBitValidatePlies] 📡 PUT /end-bits/jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode, partName, data,
      });
      try {
        const res = await validateEndBitActualPlies(jobId, barcode, partName, data);
        console.log('[useEndBitValidatePlies] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useEndBitValidatePlies] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });








export const useEndBitDeleteBarcode = () =>
  useMutation< boolean,Error,
    { jobId: string; barcode: string; partName: string; data: EndBitJobRequest }
  >({
    mutationFn: async ({ jobId, barcode, partName, data }) => {
      console.log('[useEndBitDeleteBarcode] 📡 DELETE /end-bits/jobs/{jobId}/barcodes/{barcode}', {
        jobId, barcode, partName,
      });
      try {
        const res = await removeEndBitBarcode(jobId, barcode, partName, data);
        console.log('[useEndBitDeleteBarcode] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useEndBitDeleteBarcode] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });





export const useCompleteEndBitJob = () =>
  useMutation<boolean, Error, { jobId: string; data: EndBitJobRequest }>({
    mutationFn: async ({ jobId, data }) => {
      console.log('[useCompleteEndBitJob] 📡 PUT /end-bits/jobs/{jobId}', { jobId, data });
      try {
        const res = await completeEndBitJob(jobId, data);
        console.log('[useCompleteEndBitJob] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useCompleteEndBitJob] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
