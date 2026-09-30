import type { Item } from './common';
import type { FetchPartsDetails } from './leader';
import type { Ratio } from './leader';

export interface AssignEndBitData {
  layNumber: string;
  itemCode: string;
  quantities: Record<string, string>;
  selectedParts: string[];   // list of selected partNames — mirrors Java partList
}

export interface EditEndBitJobModalProps {
  visible: boolean;
  ratios: Ratio[];
  items: Item[];
  layNumbers: string[];
  parts: FetchPartsDetails[];          // from GET /parts/{ocNo}
  isPartsLoading?: boolean;
  onDismiss: () => void;
  onSubmit: (data: AssignEndBitData) => void;
}
