import type { BundleDetail } from './leader';
import type { PrintJob } from './print';

export interface EditBundleModalProps {
  visible: boolean;
  ocNumber: string;
  jobId: string;
  totalQuantity: number;
  onDismiss: () => void;
  onSubmit: (rows: BundleDetail[]) => void;
  /** When provided the modal will print barcodes on submit */
  printJob?: PrintJob;
}

export interface BundleRowProps {
  row: BundleDetail;
  countries: string[];
  onQuantityChange: (v: string) => void;
  onCountryChange: (v: string) => void;
  onRemove?: () => void;
}
