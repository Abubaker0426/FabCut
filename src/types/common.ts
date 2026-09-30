export type Role           = 'LEADER' | 'FOLLOWER';
export type JobType        = 'NORMAL' | 'END_BIT';
export type FollowerStatus = 'IDLE' | 'BUSY' | 'DONE';

// ─── Shared item / ratio primitives ──────────────────────────────────────────

export interface Item {
  itemCode: string;
  itemDesc: string;
}

export interface RatioDetail {
  ratioNumber: number;
  size: string;
  ratioQty: number;
  ratio: number;
}
