export interface SizeDetailBarcode {
  barcode: string;
  part: string;
}

export interface SizeDetail {
  size: string;
  augmentedSize?: string;
  barcodeDetails: SizeDetailBarcode[];
}

export interface PrintOcLayDetail {
  size: string;
  augmentedSize?: string;
  fitType: string;
  bundleName: string;
  quantity: number;
  totalQuantity: number;
  itemDesc: string;
}

export interface PrintJob {
  ocNumber: string;
  style: string;
  layNumber: number;
  /** One entry per size — must align index-for-index with sizeDetails */
  augmentedSizeList: PrintOcLayDetail[];
  sizeDetails: SizeDetail[];
}
