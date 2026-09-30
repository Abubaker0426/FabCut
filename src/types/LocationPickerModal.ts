export interface LocationPickerModalProps {
  visible: boolean;
  /** Array of location name strings (Java: List<String>) */
  locations: string[];
  onSelect: (location: string) => void;
  onDismiss: () => void;
}
