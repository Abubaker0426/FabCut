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
        console.log('[useFetchAssignedJobs] 📋 Raw data:', JSON.stringify(res.data, null, 2));

        const grouped = getUniqueOcLay(res.data);
        console.log('[useFetchAssignedJobs] 📦 Grouped OcLays:', grouped.length);
        grouped.forEach((ocLay, i) => {
          console.log(
            `[useFetchAssignedJobs]   [${i}] jobId=${ocLay.jobId} ocNo=${ocLay.ocNo}` +
              ` lay=${ocLay.lay} fitType=${ocLay.fitType} tableNum=${ocLay.tableNum}` +
              ` details=${ocLay.details.length}`,
          );
          ocLay.details.forEach((d, j) => {
            console.log(
              `[useFetchAssignedJobs]     detail[${j}] size=${d.size}` +
                ` qty=${d.quantity} ratio=${d.ratio}` +
                ` itemCode=${d.itemCode ?? '—'} itemDesc=${d.itemDesc ?? '—'}`,
            );
          });
        });

        return grouped;
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
        jobId: String(row.jobId),
        tableNum: row.tableNum,
        ocNo: row.ocNo,
        lay: row.lay,
        fitType: row.fitType,
        itemDescription: `${row.itemCode} - ${row.itemDesc}`,
        details: [],
      });
    }

    const qty = Number(row.quantity); // server sends "41" as string — coerce to number
    if (qty > 0) {
      const detail: OcLayDetail = {
        size: row.size,
        quantity: qty,
        completedQty: 0,
        ratio: row.ratio,
        itemCode: row.itemCode,
        itemDesc: row.itemDesc,
      };
      map.get(key)!.details.push(detail);
    }
  }

  return Array.from(map.values());
}
