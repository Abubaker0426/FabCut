import type { RefreshControlProps } from 'react-native';
import type { EndBitJobDetail, ScanBarcode } from './follower';

export interface EndBitJobDetailTabProps {
  jobDetail: EndBitJobDetail;
  ocNumber: string;
  itemCode: string;
  itemDesc: string;
  assignedQty: number;
  cutQty: number;
  numOfPlies: number;
  actualPlies: number;
  scannedBarcodes: ScanBarcode[];
  validationStates?: Record<string, 'idle' | 'validating' | 'valid' | 'error'>;
  onActualPliesChange: (barcode: string, value: string) => void;
  onValidate?: (barcode: string, actualPlies: number) => void;
  onDelete?: (barcode: string) => void;
  onReasonChange?: (barcode: string, reason: string) => void;
  refreshControl?: React.ReactElement<RefreshControlProps>;
}
