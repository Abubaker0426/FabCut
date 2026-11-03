import { useQuery, useMutation } from '@tanstack/react-query';
import { fetchCountries, fetchPartsForOC, addBarcodesForOC } from '@/lib/api';
import type { FetchPartsResponse, AddBarcodesRequest } from '@/types/leader';

export const useFetchCountries = (ocNo: string, enabled: boolean) =>
  useQuery<string[]>({
    queryKey: ['countries', ocNo],
    queryFn: async () => {
      console.log('[useFetchCountries] 📡 GET /parts/{ocNo}/country', { ocNo });
      try {
        const res = await fetchCountries(ocNo);
        console.log('[useFetchCountries] ✅ Countries:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchCountries] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!ocNo && enabled,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

/**
 * Pre-fetches parts count when the modal opens so we can show
 * the expected label count before the user taps Submit & Print.
 * Same endpoint as useFetchPartsForPrint but as a query (not mutation).
 */
export const useFetchPartsCount = (ocNo: string, location: string, enabled: boolean) =>
  useQuery<number>({
    queryKey: ['partsCount', ocNo, location],
    queryFn: async () => {
      const res = await fetchPartsForOC(ocNo, location);
      return res.data.parts?.length ?? 0;
    },
    enabled: !!ocNo && !!location && enabled,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

export const useFetchPartsForPrint = () =>
  useMutation<FetchPartsResponse, Error, { ocNo: string; location: string }>({
    mutationFn: async ({ ocNo, location }) => {
      console.log('[useFetchPartsForPrint] 📡 GET /parts/{ocNo}', { ocNo, location });
      try {
        const res = await fetchPartsForOC(ocNo, location);
        console.log('[useFetchPartsForPrint] ✅ Parts:', res.data.parts?.length);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchPartsForPrint] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });

export const useSaveBarcodes = () =>
  useMutation<boolean, Error, { ocNo: string; data: AddBarcodesRequest }>({
    mutationFn: async ({ ocNo, data }) => {
      console.log('[useSaveBarcodes] 📡 POST /parts/barcode/{ocNo}', { ocNo, data });
      try {
        const res = await addBarcodesForOC(ocNo, data);
        console.log('[useSaveBarcodes] ✅ Saved:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useSaveBarcodes] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
