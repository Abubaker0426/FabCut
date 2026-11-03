import type { BundleDetail, OcLay } from './leader';

export interface EditBundleModalProps {
  visible: boolean;
  job: OcLay | null; // full job info — needed for barcode generation
  onDismiss: () => void;
  onSubmit: () => void; // called after successful print
}

export interface BundleRowProps {
  row: BundleDetail;
  countries: string[];
  onQuantityChange: (v: string) => void;
  onCountryChange: (v: string) => void;
  onRemove?: () => void;
}
