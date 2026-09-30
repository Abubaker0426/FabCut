import { useQuery } from '@tanstack/react-query';
import { fetchJobDetails } from '@/lib/api';
import type { JobDetailsResponse } from '@/types/leader';

export const useFetchJobDetails = (markerUnique: string) =>
  useQuery<JobDetailsResponse>({
    queryKey: ['jobDetails', markerUnique],
    queryFn: async () => {
      console.log('[useFetchJobDetails] 📡 GET /ratios/{marker}', { markerUnique });
      try {
        const res = await fetchJobDetails(markerUnique);
        console.log(
          '[useFetchJobDetails] ✅ Ratios received:',
          res.data.ratios?.length,
          'rows',
        );
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchJobDetails] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!markerUnique,
    staleTime: 0,   // always re-fetch when marker changes
    retry: false,
  });
