import { useQuery, keepPreviousData } from '@tanstack/react-query';
import { fetchFitTypes } from '@/lib/api';

export const useValidateOc = (
  ocNo: string,
  location: string | null,
  triggered: boolean,
) =>
  useQuery<string[]>({
    queryKey: ['fitTypes', ocNo, location],
    queryFn: async () => {
      console.log('[useValidateOc] 📡 GET /ratios/{ocNo}/fitTypes', { ocNo, location });
      try {
        const res = await fetchFitTypes(ocNo, location!);
        console.log('[useValidateOc] ✅ OC valid — fit types:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useValidateOc] ❌ OC invalid or network error:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled:          triggered && ocNo.trim().length > 0 && !!location,
    retry:            false,
    staleTime:        30_000,        // cache result for 30s — same OC doesn't re-fetch
    placeholderData:  keepPreviousData, // isSuccess stays true while re-fetching
  });
