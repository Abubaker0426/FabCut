import type { ReactNode } from 'react';

export interface AppSheetModalProps {
  title: string;
  badge?: number;
  snapPoints?: (string | number)[];
  initialIndex?: number;
  showBackdrop?: boolean;
  onClose?: () => void;
  scrollable?: boolean;
  children: ReactNode;
}
