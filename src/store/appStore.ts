import { create } from 'zustand';

import type { Role } from '../types/common';
import type { MarkerItem } from '../types/leader';

interface AppState {
  // Device / session
  deviceId: string | null;
  role: Role | null;
  location: string | null;
  tableNumber: number;
  isRegistered: boolean;

  // Leader navigation — avoids Expo Router corrupting special chars in URL params
  selectedMarker: MarkerItem | null;

  // Follower scanner — stores last scanned code so FollowerScreen can read it
  // after router.back() from /scanner. Same pattern as selectedMarker.
  lastScannedCode: string | null;

  // Actions
  setDeviceId: (id: string) => void;
  setRole: (role: Role) => void;
  setLocation: (location: string) => void;
  setTableNumber: (n: number) => void;
  setRegistered: (v: boolean) => void;
  setSelectedMarker: (marker: MarkerItem | null) => void;
  setLastScannedCode: (code: string | null) => void;
  reset: () => void;
}

const defaultState = {
  deviceId: null,
  role: null,
  location: null,
  tableNumber: 0,
  isRegistered: false,
  selectedMarker: null,
  lastScannedCode: null,
};

export const useAppStore = create<AppState>((set) => ({
  ...defaultState,

  setDeviceId:        (deviceId)        => set({ deviceId }),
  setRole:            (role)            => set({ role }),
  setLocation:        (location)        => set({ location }),
  setTableNumber:     (tableNumber)     => set({ tableNumber }),
  setRegistered:      (isRegistered)    => set({ isRegistered }),
  setSelectedMarker:  (selectedMarker)  => set({ selectedMarker }),
  setLastScannedCode: (lastScannedCode) => set({ lastScannedCode }),
  reset: () => set(defaultState),
}));
