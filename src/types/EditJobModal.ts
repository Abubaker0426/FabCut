import type { Item } from './common';
import type { Ratio } from './leader';

export interface AssignJobItemData {
  itemCode: string;
  shade: string;
  shrinkage: string;
  pattern: string;
  quantities: Record<string, string>;
  layLength: string;
}

export interface AssignJobData {
  ratioNumber: number;
  ratios: Record<string, string>;
  items: AssignJobItemData[];
}

export interface EditJobModalProps {
  visible: boolean;
  ratioNumber: number | null;
  ratios: Ratio[];
  items?: Item[];
  onDismiss: () => void;
  onSubmit: (data: AssignJobData) => void;
}

export interface ItemBlockProps {
  itemCode: string;
  itemDesc?: string;
  sizes: string[];
  sizeChunks: string[][];
  data: AssignJobItemData;
  total: number;
  onRemove: () => void;
  onFieldChange: (field: 'shade' | 'shrinkage' | 'pattern' | 'layLength', v: string) => void;
  onQtyChange: (size: string, v: string) => void;
}