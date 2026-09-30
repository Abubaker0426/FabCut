export interface Layout {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface CustomDropdownProps {
  data: string[];
  placeholder: string;
  selectedValue?: string;
  disabled?: boolean;
  onSelect: (item: string) => void;
}