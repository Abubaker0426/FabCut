import { create } from 'zustand';

import type { Role } from '@/types';

interface AppState {
  // Device / session
  deviceId: string | null;
  role: Role | null;
  location: string | null;
  tableNumber: number;
  isRegistered: boolean;

  // Actions
  setDeviceId: (id: string) => void;
  setRole: (role: Role) => void;
  setLocation: (location: string) => void;
  setTableNumber: (n: number) => void;
  setRegistered: (v: boolean) => void;
  reset: () => void;
}

const defaultState = {
  deviceId: null,
  role: null,
  location: null,
  tableNumber: 0,
  isRegistered: false,
};

export const useAppStore = create<AppState>((set) => ({
  ...defaultState,

  setDeviceId: (deviceId) => set({ deviceId }),
  setRole: (role) => set({ role }),
  setLocation: (location) => set({ location }),
  setTableNumber: (tableNumber) => set({ tableNumber }),
  setRegistered: (isRegistered) => set({ isRegistered }),
  reset: () => set(defaultState),
}));
