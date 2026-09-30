import type { RefreshControlProps } from 'react-native';
import type { NormalJobDetail, ScanBarcode } from './follower';

export interface JobDetailTabProps {
  jobDetail: NormalJobDetail;
  ocNumber: string;
  layLength: number | string;
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
