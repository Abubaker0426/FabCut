// ─── Enums ───────────────────────────────────────────────────────────────────

export type Role = 'LEADER' | 'FOLLOWER';
export type JobType = 'NORMAL' | 'END_BIT';
export type FollowerStatus = 'IDLE' | 'BUSY' | 'DONE';

// ─── Registration ────────────────────────────────────────────────────────────

export interface IsDevRegResponse {
  devRegStatus: boolean;
  role: Role;
}

export interface RegisterRequest {
  deviceId: string;
  role: Role;
  tableNumber: number;
  location: string;
}

// ─── Location ────────────────────────────────────────────────────────────────

export interface Location {
  locationId: string;
  locationName: string;

}

// ─── Marker / OC ─────────────────────────────────────────────────────────────

export interface Item {
  itemCode: string;
  itemDesc: string;
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

// ─── Job / Ratios ─────────────────────────────────────────────────────────────

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

// ─── Follower ────────────────────────────────────────────────────────────────

export interface Follower {
  deviceId: string;
  status: FollowerStatus;
  tableNumber: number;
}

// ─── Assigned Jobs (Leader view) ─────────────────────────────────────────────

export interface OcLayDetail {
  size: string;
  quantity: number;
  completedQty: number;
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

// ─── Follower Job (Follower view) ─────────────────────────────────────────────

export interface RatioDetail {
  ratioNumber: number;
  size: string;
  ratioQty: number;
  ratio: number;
}

export interface NormalJobDetail {
  itemCode: string;
  itemDesc: string;
  layNumber: number;
  ratioDetails: RatioDetail[];
}

export interface FetchFollowerJobResponse {
  jobId: string;
  ocNumber: string;
  layLength: number;
  fitType: string;
  assignedQty: number;
  cutQty: number;
  numOfPlies: number;
  actualPlies: number;
  jobDetails: NormalJobDetail[];
  jobType: JobType;
}

export interface EndBitJobDetail {
  partName: string;
  layLength: number;
  ratioDetails: RatioDetail[];
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

// ─── Scanned Barcode ──────────────────────────────────────────────────────────

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
  plies: number;
}

// ─── Assign Job ───────────────────────────────────────────────────────────────

export interface AssignJobDetailItem {
  itemCode: string;
  shade: string;
  shrinkage: number;
  pattern: string;
  quantities: Record<string, number>; // size -> qty
}

export interface AssignJobRequest {
  markerUnique: string;
  layLength: number;
  followerId: string;
  jobDetails: AssignJobDetailItem[];
}

export interface AssignEndBitJobRequest {
  markerId: string;
  layNumber: number;
  itemCode: string;
  followerId: string;
  jobDetails: AssignJobDetailItem[];
}

// ─── Bundle Parts ─────────────────────────────────────────────────────────────

export interface FetchPartsDetails {
  partsUnique: string;
  partName: string;
  isSelected: boolean;
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

// ─── Validate Plies ───────────────────────────────────────────────────────────

export interface ValidateActualPliesRequest {
  actualPlies: number;
  deviceId: string;
  location: string;
}
