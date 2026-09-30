export interface OcFormProps {
  ocNumber: string;
  setOcNumber: (v: string) => void;
  onOcBlur: () => void;
  fitType: string;
  setFitType: (v: string) => void;
  fitTypes: string[];
  ocValid: boolean | null;
  onSubmit: () => void;
}
