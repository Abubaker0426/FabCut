import { useQuery } from '@tanstack/react-query';
import { fetchPartsForOC } from '@/lib/api';
import type { FetchPartsResponse } from '@/types/leader';

export const useFetchPartsForOC = (
  ocNo: string,
  location: string | null,
  enabled: boolean,
) =>
  useQuery<FetchPartsResponse>({
    queryKey: ['partsForOC', ocNo, location],
    queryFn: async () => {
      console.log('[useFetchPartsForOC] 📡 GET /parts/{ocNo}', { ocNo, location });
      try {
        const res = await fetchPartsForOC(ocNo, location!);
        console.log('[useFetchPartsForOC] ✅ Parts:', res.data.parts.length);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchPartsForOC] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!ocNo && !!location && enabled,
    staleTime: 0,
    retry: false,
  });
