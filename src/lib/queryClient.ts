import { QueryClient, QueryCache, MutationCache } from '@tanstack/react-query';
import { toast } from '@/lib/toast';

export const queryClient = new QueryClient({
  queryCache: new QueryCache({
    onError: (error, query) => {
      if (query.meta?.silent) return;
      toast.apiError(error, 'Error');
    },
  }),
  mutationCache: new MutationCache({
    onError: (error, _vars, _ctx, mutation) => {
      if (mutation.meta?.silent) return;
      toast.apiError(error, 'Error');
    },
  }),
  defaultOptions: {
    queries: { retry: false, staleTime: 1000 * 60 * 5 },
  },
});
