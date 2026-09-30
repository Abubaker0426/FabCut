import { useMutation } from '@tanstack/react-query';
import { fetchMarkers } from '@/lib/api';
import type { MarkerResponse } from '@/types/leader';


export const useFetchMarkers = () =>
  useMutation<
    MarkerResponse[],
    Error,
    { ocNo: string; location: string; fitType: string }
  >({
    mutationFn: async ({ ocNo, location, fitType }) => {
      console.log('[useFetchMarkers] 📡 GET /ratios/markers/{ocNo}', { ocNo, location, fitType });
      try {
        const res = await fetchMarkers(ocNo, location, fitType);
        console.log('[useFetchMarkers] ✅ Markers received:', res.data.length, 'items');
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchMarkers] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
