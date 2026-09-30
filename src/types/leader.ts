import type { Item, RatioDetail } from './common';


export interface OcItemCode {
  itemCode: string;
  itemDesc: string;
}

export interface MarkerResponse {
  markerUnique: string;
  shrinkage: string;
  items: OcItemCode[];
}

export interface Marker {
  markerUnique: string;
  shrinkage: number;
  items: Item[];
  ocNumber?: string;
}
export interface MarkerItem {
  markerUnique: string;
  shrinkage: number;
  items: Item[];
  ocNumber: string;
}

export interface FitType {
  fitType: string;
}

// ─── Ratio / Job rows (LeaderJobsScreen) ─────────────────────────────────────

export interface Ratio {
  ratioNumber: number;
  size: string;
  ratioQty: number;
  ratio: number;
}

export interface SizeQuantity {
  size: string;
  qty: number;
  completedQty: number;
}

export interface LayLength {
  ratioNumber: number;
  layLength: number;
}

export interface JobDetails {
  ratios: Ratio[];
  itemDesc: string;
  custName: string;
  styleNo: string;
  sizeQuantities: SizeQuantity[];
  layLengths: LayLength[];
}

/**
 * Full job details returned from GET /ratios/{marker}.
 * Mirrors Java: JobDetails  (note: sizeQtys is the serialized name, not sizeQuantities)
 */
export interface JobDetailsResponse {
  ratios: Ratio[];
  itemDesc: string;
  custName: string;
  styleNo: string;
  sizeQtys: SizeQuantity[];    // Java @SerializedName("sizeQtys")
  layLengths: LayLength[];
}

// ─── Follower device (shown in bottom sheet) ──────────────────────────────────

export interface Follower {
  deviceId: string;
  status: 'Idle' | 'Busy';   // Java Status enum: @SerializedName("Idle") / @SerializedName("Busy")
  tableNumber: number;
}

// ─── Assigned jobs list (bottom sheet Jobs tab) ───────────────────────────────

export interface OcLayDetail {
  size: string;
  quantity: number;
  completedQty: number;
  itemDesc?: string;
}

export interface OcLay {
  jobId: string;
  tableNum: number;
  ocNo: string;
  lay: number;
  fitType: string;
  itemDescription: string;
  details: OcLayDetail[];
}

/**
 * Single row from the assigned-jobs list.
 * Mirrors Java: JobListResponse
 */
export interface JobListItem {
  jobId: number;
  tableNum: number;
  ocNo: string;
  fitType: string;
  itemCode: string;
  itemDesc: string;
  size: string;
  quantity: number;
  ratio: number;
  lay: number;
}

// ─── Assign job request ───────────────────────────────────────────────────────

export interface AssignJobRatioDetail {
  size: string;
  quantity: number;
  ratio: number;
}

export interface AssignJobDetailItem {
  itemCode: string;
  itemDesc: string;
  shade: string;
  shrinkage: string;
  pattern: string;
  ratioDetails: AssignJobRatioDetail[];
}

/**
 * Body for POST /jobs/{deviceId}.
 * Mirrors Java: AssignJobRequest
 */
export interface AssignJobRequest {
  markerUnique: string;
  location: string;
  layLength: number;
  jobDetails: AssignJobDetailItem[];
}

export interface AssignEndBitRatioDetail {
  size: string;
  quantity: number;
}

export interface AssignEndBitJobDetail {
  partName: string;
  layLength: number;
  ratioDetails: AssignEndBitRatioDetail[];
}

/**
 * Body for POST /end-bits/jobs/{deviceId}.
 * Mirrors Java: AssignEndBitJobRequest
 */
export interface AssignEndBitJobRequest {
  markerId: string;
  location: string;
  layNumber: number;
  itemCode: string;
  itemDesc: string;
  jobDetails: AssignEndBitJobDetail[];
}

// ─── Bundle Parts ─────────────────────────────────────────────────────────────

export interface FetchPartsDetails {
  partsUnique: number;   // Java: Long
  part: string;          // Java: @SerializedName("part")
  isSelected: number;    // Java: int 0 or 1 — NOT boolean
}

export interface FetchPartsResponse {
  ocNo: string;
  style: string;
  parts: FetchPartsDetails[];
}

export interface BundleDetail {
  quantity: number;
  country: string;
}

/**
 * Body for PUT /parts/{ocNo}.
 * Mirrors Java: PutPartsDetailsRequest
 */
export interface UpdateBundlePartsRequest {
  parts: FetchPartsDetails[];
}

/**
 * Barcode entry inside AddBarcodesRequest.
 * Mirrors Java: SizeDetail (inner class of AddPartStickersRequest)
 */
export interface SizeDetailBarcode {
  barcode: string;
  part: string;
}

export interface SizeDetailRequest {
  size: string;
  augmentedSize?: string;
  barcodeDetails: SizeDetailBarcode[];
}

/**
 * Body for POST /parts/barcode/{ocNo}.
 * Mirrors Java: AddPartStickersRequest
 */
export interface AddBarcodesRequest {
  location: string;
  fitType: string;
  lay: number;
  sizeDetails: SizeDetailRequest[];
  jobId: number;
}

// ─── Follower details (FollowerDetailsScreen) ────────────────────────────────

export interface FollowerJobDetail {
  itemCode: string;
  itemDesc: string;
  layNumber: number;
  ratioDetails: RatioDetail[];
}

export interface FollowerEndBitDetail {
  partName: string;
  layLength: number;
  ratioDetails: RatioDetail[];
}
