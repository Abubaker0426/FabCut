export interface SizeDetailBarcode {
  partsUnique: number; // Java: Long — required by server
  barcode: string;
  part: string;
}

export interface SizeDetail {
  itemDesc: string;
  itemCode: string;
  size: string;
  augmentedSize: string;
  quantity: number;
  countryCode: string;
  countryName: string;
  bundleNumber: number;
  barcodeDetails: SizeDetailBarcode[];
}

export interface PrintOcLayDetail {
  size: string;
  augmentedSize?: string;
  fitType: string;
  bundleName: string;
  bundleNumber: number;
  quantity: number;
  totalQuantity: number;
  itemCode: string;
  itemDesc: string;
  country: string; // full country string e.g. "IN | INDIA"
}

export interface PrintJob {
  ocNumber: string;
  style: string;
  layNumber: number;
  /** One entry per size — must align index-for-index with sizeDetails */
  augmentedSizeList: PrintOcLayDetail[];
  sizeDetails: SizeDetail[];
}
