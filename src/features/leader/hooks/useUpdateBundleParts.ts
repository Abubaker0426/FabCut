import { useMutation } from '@tanstack/react-query';
import { updateSelectedBundleParts } from '@/lib/api';
import type { FetchPartsDetails } from '@/types/leader';

export const useUpdateBundleParts = () =>
  useMutation<
    boolean,
    Error,
    { ocNo: string; parts: FetchPartsDetails[] }
  >({
    mutationFn: async ({ ocNo, parts }) => {
      console.log('[useUpdateBundleParts] 📡 PUT /parts/{ocNo}', {
        ocNo,
        parts: parts.map((p) => `${p.part}:${p.isSelected}`),
      });
      try {
        const res = await updateSelectedBundleParts(ocNo, { parts });
        console.log('[useUpdateBundleParts] ✅ Response:', res.data);
        return res.data;
      } catch (err: any) {
        console.error(
          '[useUpdateBundleParts] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
  });
