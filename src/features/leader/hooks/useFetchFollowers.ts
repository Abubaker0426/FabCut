import { useQuery } from '@tanstack/react-query';
import { fetchFollowers } from '@/lib/api';
import type { Follower } from '@/types/leader';


export const useFetchFollowers = (location: string | null, enabled: boolean) =>
  useQuery<Follower[]>({
    queryKey: ['followers', location],
    queryFn: async () => {
      console.log('[useFetchFollowers] 📡 GET /followers/{location}', { location });
      try {
        const res = await fetchFollowers(location!);
        console.log('[useFetchFollowers] ✅ Followers:', res.data.length);
        console.log('[useFetchFollowers] 📋 Raw statuses:', res.data.map(f => `Table ${f.tableNumber}: "${f.status}"`));
        return res.data;
      } catch (err: any) {
        console.error(
          '[useFetchFollowers] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!location && enabled,
    staleTime: 0,   // always re-fetch when sheet opens
    retry: false,
  });
