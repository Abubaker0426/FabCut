/**
 * Leader dummy data — remove after API integration.
 * Keep all mock data here so screens stay clean.
 */
import type { Follower, Marker, OcLay, Ratio } from '@/types';

export const MOCK_FIT_TYPES: string[] = ['Regular', 'Slim', 'Relaxed'];

export const MOCK_MARKERS: Marker[] = [
  {
    markerUnique: 'MKR-001',
    shrinkage: 2.5,
    ocNumber: 'OC2024001',
    items: [
      { itemCode: 'IC-001', itemDesc: 'Men Shirt - Blue' },
      { itemCode: 'IC-002', itemDesc: 'Men Shirt - White' },
    ],
  },
  {
    markerUnique: 'MKR-002',
    shrinkage: 3.0,
    ocNumber: 'OC2024001',
    items: [{ itemCode: 'IC-003', itemDesc: 'Men Trouser - Black' }],
  },
];

export const MOCK_RATIOS: Ratio[] = [
  { ratioNumber: 1, size: 'S',  ratioQty: 20, ratio: 1   },
  { ratioNumber: 1, size: 'M',  ratioQty: 40, ratio: 2   },
  { ratioNumber: 1, size: 'L',  ratioQty: 30, ratio: 1.5 },
  { ratioNumber: 1, size: 'XL', ratioQty: 10, ratio: 0.5 },
  { ratioNumber: 2, size: 'S',  ratioQty: 15, ratio: 1   },
  { ratioNumber: 2, size: 'M',  ratioQty: 35, ratio: 2   },
  { ratioNumber: 2, size: 'L',  ratioQty: 25, ratio: 1.5 },
];

export const MOCK_FOLLOWERS: Follower[] = [
  { deviceId: 'D1', tableNumber: 1, status: 'IDLE' },
  { deviceId: 'D2', tableNumber: 2, status: 'BUSY' },
  { deviceId: 'D3', tableNumber: 3, status: 'DONE' },
  { deviceId: 'D4', tableNumber: 4, status: 'IDLE' },
  { deviceId: 'D5', tableNumber: 5, status: 'BUSY' },
  { deviceId: 'D6', tableNumber: 6, status: 'IDLE' },
];

export const MOCK_ASSIGNED_JOBS: OcLay[] = [
  {
    jobId: 'J1',
    tableNum: 2,
    ocNo: 'OC2024001',
    lay: 3,
    fitType: 'Regular',
    itemDescription: 'Men Shirt - Blue / IC-001',
    details: [],
  },
  {
    jobId: 'J2',
    tableNum: 5,
    ocNo: 'OC2024002',
    lay: 1,
    fitType: 'Slim',
    itemDescription: 'Men Trouser - Black / IC-003',
    details: [],
  },
  {
    jobId: 'J2',
    tableNum: 5,
    ocNo: 'OC2024002',
    lay: 1,
    fitType: 'Slim',
    itemDescription: 'Men Trouser - Black / IC-003',
    details: [],
  },
];

export const MOCK_LAY_NUMBERS: string[] = ['1', '2', '3', '4'];

export const MOCK_COUNTRIES: string[] = [
  'India', 'USA', 'UK', 'Germany', 'France', 'Japan', 'Australia',
];
