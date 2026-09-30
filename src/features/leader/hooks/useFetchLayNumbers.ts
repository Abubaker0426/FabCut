import { useQuery } from '@tanstack/react-query';
import { fetchLayNumbers } from '@/lib/api';

export const useFetchLayNumbers = (
  markerId: string,
  itemCode: string,
  location: string | null,
  enabled: boolean,
) =>
  useQuery<number[]>({
    queryKey: ['layNumbers', markerId, itemCode, location],
    queryFn: async () => {
      console.log('[useFetchLayNumbers] 📡 GET /end-bits/lay-numbers/{markerId}', {
        markerId, itemCode, location,
      });
      try {
        const res = await fetchLayNumbers(markerId, itemCode, location!);
        console.log('[useFetchLayNumbers] ✅ Lay numbers:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchLayNumbers] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!markerId && !!itemCode && !!location && enabled,
    staleTime: 0,
    retry: false,
  });
