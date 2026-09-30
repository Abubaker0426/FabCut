import { useQuery } from '@tanstack/react-query';
import { fetchAssignedJobs } from '@/lib/api';
import type { JobListItem, OcLay, OcLayDetail } from '@/types/leader';


export const useFetchAssignedJobs = (location: string | null, enabled: boolean) =>
  useQuery<OcLay[]>({
    queryKey: ['assignedJobs', location],
    queryFn: async () => {
      console.log('[useFetchAssignedJobs] 📡 GET /jobs/fetch/{location}', { location });
      try {
        const res = await fetchAssignedJobs(location!);
        console.log('[useFetchAssignedJobs] ✅ Raw rows:', res.data.length);
        return getUniqueOcLay(res.data);
      } catch (err: any) {
        console.error(
          '[useFetchAssignedJobs] ❌ Failed:',
          err?.response?.status,
          err?.response?.data ?? err?.message,
        );
        throw err;
      }
    },
    enabled: !!location && enabled,
    staleTime: 0,
    retry: false,
  });

// ─── mirrors Java CommonUtils.getUniqueOcLay() ───────────────────────────────
// Groups flat JobListItem[] rows into OcLay[] (one entry per jobId+itemCode+fitType+lay)
function getUniqueOcLay(rows: JobListItem[]): OcLay[] {
  const map = new Map<string, OcLay>();

  for (const row of rows) {
    // Key mirrors Java's CommonUtilsJobUnique(ocNo, lay, fitType, itemCode)
    const key = `${row.jobId}-${row.ocNo}-${row.lay}-${row.fitType}-${row.itemCode}`;

    if (!map.has(key)) {
      map.set(key, {
        jobId:           String(row.jobId),
        tableNum:        row.tableNum,
        ocNo:            row.ocNo,
        lay:             row.lay,
        fitType:         row.fitType,
        itemDescription: `${row.itemCode} - ${row.itemDesc}`,
        details:         [],
      });
    }

    if (row.quantity > 0) {
      const detail: OcLayDetail = {
        size:        row.size,
        quantity:    row.quantity,
        completedQty: 0,
        itemDesc:    row.itemDesc,
      };
      map.get(key)!.details.push(detail);
    }
  }

  return Array.from(map.values());
}
