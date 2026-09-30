/**
 * auth.ts
 * Types used in the auth / registration flow (MainScreen, LocationPickerModal).
 */
import type { Role } from './common';

// ─── Device registration ──────────────────────────────────────────────────────

export interface IsDevRegResponse {
  devRegStatus: boolean;
  role: Role;
}

/** Mirrors Java RegDevRequest — deviceId is a path param, not in body */
export interface RegisterRequest {
  location: string;
  role: Role;
  tableNumber: number; // 0 for LEADER
}

/** Mirrors Java UnregisterDevRequest */
export interface UnregisterRequest {
  location: string;
  role: Role;
}

// ─── Location validation ──────────────────────────────────────────────────────

/**
 * The Java backend returns a plain List<String> of location names.
 * We alias it here so the rest of the app stays readable.
 */
export type Location = string;

export interface ValidateLocationRequest {
  latitude: number;
  longitude: number;
}
