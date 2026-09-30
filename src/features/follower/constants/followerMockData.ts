import type {  FetchFollowerEndBitJobResponse,  ScanBarcode,} from '@/types/follower';

export const MOCK_ENDBIT_JOB: FetchFollowerEndBitJobResponse = {
  endBitJobId: 'EB001',
  ocNumber: 'OC2024001',
  itemCode: 'IC-001',
  itemDesc: 'Men Shirt - Blue',
  assignedQty: 30,
  cutQty: 10,
  numOfPlies: 8,
  actualPlies: 6,
  jobDetails: [
    {
      partName: 'Front Panel',
      layLength: 60,
      ratioDetails: [
        { size: 'S', quantity: 10, ratio: 1 },
        { size: 'M', quantity: 15, ratio: 1 },
      ],
    },
    {
      partName: 'Back Panel',
      layLength: 58,
      ratioDetails: [
        { size: 'S', quantity: 10, ratio: 1 },
        { size: 'M', quantity: 15, ratio: 1 },
      ],
    },
  ],
};

export const MOCK_BARCODES: ScanBarcode[] = [
  { barcode: 'BC-00001', jobId: 'J001', expectedPlies: 24, actualPlies: 0,  itemCode: 'IC-001', validated: true },
  // { barcode: 'BC-00002', jobId: 'J001', expectedPlies: 24, actualPlies: 22, itemCode: 'IC-001', validated: true  },
];

// Change to 'END_BIT' or 'NONE' to test those states
export const DEMO_JOB_TYPE: 'NORMAL' | 'END_BIT' | 'NONE' = 'NONE';
