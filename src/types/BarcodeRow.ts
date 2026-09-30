import type { ScanBarcode } from './follower';

export type ValidationState = 'idle' | 'validating' | 'valid' | 'error';

export interface BarcodeRowProps {
  item: ScanBarcode;
  validationState?: ValidationState;
  onActualPliesChange: (barcode: string, value: string) => void;
  onValidate?: (barcode: string, actualPlies: number) => void;
  onDelete?: (barcode: string) => void;
  onReasonChange?: (barcode: string, reason: string) => void;
}
