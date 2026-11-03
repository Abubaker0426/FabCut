/** * bundleBarcode.ts
 * * Pure barcode-generation helpers extracted from EditBundleModal.
 * No React dependency — can be tested independently.*/
import { customAlphabet } from 'nanoid/non-secure';

import type { BundleDetail, FetchPartsDetails, OcLay } from '@/types/leader';
import type { PrintJob, PrintOcLayDetail, SizeDetail, SizeDetailBarcode } from '@/types/print';

// Mirrors Java: NanoIdUtils.randomNanoId(random, AppConstants.ALPHABETS.toCharArray(), 10)
const ALPHABETS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
const nanoid = customAlphabet(ALPHABETS, 10);

// ─── Exported helpers ─────────────────────────────────────────────────────────

/**
 * Calculates how many bundles the leader is allowed to split into.
 * Mirrors Java: EditBundleDialogPresenter.getBundleSplitAllowedQuantity()
 */
export function getAllowedQuantity(details: OcLay['details']): number {
  for (const d of details ?? []) {
    if (d.ratio > 0) return Math.floor(d.quantity / d.ratio);
  }
  return 0;
}

/**
 * Builds the augmented size list — one entry per ratio repetition per bundle row.
 * Mirrors Java: EditBundleDialogPresenter.addAugmentedSizeToOcLayDetailList()
 */
export function buildAugmentedSizeList(job: OcLay, bundleRows: BundleDetail[]): PrintOcLayDetail[] {
  const result: PrintOcLayDetail[] = [];

  bundleRows.forEach((bundleDetail, bundleIndex) => {
    (job.details ?? []).forEach((detail) => {
      const ratio = detail.ratio ?? 1;
      if (ratio > 0) {
        for (let i = 1; i <= ratio; i++) {
          result.push({
            size: detail.size,
            augmentedSize: ratio > 1 ? getCharForNumber(i) : undefined,
            fitType: job.fitType,
            bundleName: `B${bundleIndex + 1}`,
            bundleNumber: bundleIndex + 1,
            quantity: bundleDetail.quantity,
            totalQuantity: Math.floor(detail.quantity / ratio),
            itemCode: detail.itemCode ?? '',
            itemDesc: detail.itemDesc ?? job.itemDescription,
            country: bundleDetail.country,
          });
        }
      }
    });
  });

  return result;
}

/**
 * Generates the size-detail list with NanoID barcodes per part per size.
 * Bundle 1 gets ALL parts; bundle 2+ gets only selected parts.
 * Mirrors Java: EditBundleDialogPresenter.createSizeDetailsList()
 */
export function buildSizeDetailsList(
  augmentedList: PrintOcLayDetail[],
  partsList: FetchPartsDetails[],
): SizeDetail[] {
  const selectedParts = partsList.filter((p) => p.isSelected === 1);

  return augmentedList.map((item) => {
    const actualParts = item.bundleNumber === 1 ? partsList : selectedParts;

    // Mirrors Java: country.split("\\|") → countryCode | countryName
    const [countryCode, countryName] = item.country
      ? item.country.split('|').map((s) => s.trim())
      : ['', ''];

    const barcodeDetails: SizeDetailBarcode[] = actualParts.map((part) => ({
      partsUnique: part.partsUnique,
      barcode: nanoid(),
      part: part.part,
    }));

    return {
      itemDesc: item.itemDesc,
      itemCode: item.itemCode,
      size: item.size,
      augmentedSize: item.augmentedSize ?? 'NA',
      quantity: item.quantity,
      countryCode,
      countryName,
      bundleNumber: item.bundleNumber,
      barcodeDetails,
    };
  });
}

/**
 * Assembles the PrintJob passed to printManager.printBarcodes().
 */
export function buildPrintJob(
  job: OcLay,
  augmentedSizeList: PrintOcLayDetail[],
  sizeDetailList: SizeDetail[],
): PrintJob {
  return {
    ocNumber: job.ocNo,
    style: job.itemDescription,
    layNumber: job.lay,
    augmentedSizeList,
    sizeDetails: sizeDetailList,
  };
}

// ─── Internal helpers ─────────────────────────────────────────────────────────

/** Mirrors Java getCharForNumber() — 1→A, 2→B, 3→C ... */
function getCharForNumber(i: number): string {
  return i > 0 && i < 27 ? String.fromCharCode(i + 64) : '';
}
