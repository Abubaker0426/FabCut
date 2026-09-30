
import type { JobType } from './common';
export interface NormalJobRatioDetail {
  size: string;
  quantity: string | number;  
  ratio: number;
}

export interface NormalJobDetail {
  itemCode: string;
  itemDesc: string;
  layNumber: number;
  ratioDetails: NormalJobRatioDetail[];
}

export interface FetchFollowerJobResponse {
  jobId: number;              // API returns number e.g. 205763
  ocNumber: string;
  layLength: string;          // API returns string e.g. "6.90"
  fitType: string;
  jobDetails: NormalJobDetail[];
  assignedQty?: number;
  cutQty?: number;
  numOfPlies?: number;
  actualPlies?: number;
  jobType?: JobType;
}

// ─── End-bit job (one tab per partName) ──────────────────────────────────────

export interface EndBitRatioDetail {
  size: string;
  quantity: string | number;  // keep consistent with NormalJobRatioDetail
  ratio: number;
}

export interface EndBitJobDetail {
  partName: string;
  layLength: number;
  ratioDetails: EndBitRatioDetail[];
}

export interface FetchFollowerEndBitJobResponse {
  endBitJobId: string;
  ocNumber: string;
  itemCode: string;
  itemDesc: string;
  assignedQty: number;
  cutQty: number;
  numOfPlies: number;
  actualPlies: number;
  jobDetails: EndBitJobDetail[];
}

// ─── Scanned barcode (BarcodeRow) ────────────────────────────────────────────

export interface ScanBarcode {
  id?: number;
  jobId: string;
  barcode: string;
  expectedPlies: number;
  actualPlies: number;
  itemCode: string;
  reason?: string;
  validated: boolean;
}

export interface ScanBarcodeResponse {
  length: number;   // fabric length of the bundle
  plies: number;    // expected ply count for this bundle
}

export interface ScanBarcodeRequest {
  itemCode: string;
}

export interface EndBitJobRequest {
  deviceId: string;
  location: string;
}

export interface ValidateActualPliesRequest {
  actualPlies: number;
  deviceId?: string;   // required for end-bit, not sent for normal job
  location?: string;   // required for end-bit, not sent for normal job
}
