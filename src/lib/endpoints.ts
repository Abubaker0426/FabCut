export const ENDPOINTS = {

  // ─── Location ─────────────────────────────────────────────────────────────
  LOCATION: '/service/validate/locations',

  // ─── Device Registration ──────────────────────────────────────────────────
  REGISTER_DEVICE: {
    CHECK:      (deviceId: string) => `/fabcut/cutting/registrations/${deviceId}`,
    REGISTER:   (deviceId: string) => `/fabcut/cutting/registrations/${deviceId}`,
    UNREGISTER: (deviceId: string) => `/fabcut/cutting/registrations/${deviceId}`,
  },

  // ─── Leader ───────────────────────────────────────────────────────────────
  LEADER: {
    // OC / Ratios / Markers
    VALIDATE_OC_AND_FETCH_ITEMS: (ocNo: string) =>     `/fabcut/cutting/ratios/${ocNo}/itemCodes`,
    GET_FIT_TYPES:               (ocNo: string) =>     `/fabcut/cutting/ratios/${ocNo}/fitTypes`,
    GET_MARKERS:                 (ocNo: string) =>     `/fabcut/cutting/ratios/markers/${ocNo}`,
    GET_JOB_DETAILS:             (marker: string) =>   `/fabcut/cutting/ratios/${marker}`,

    // Followers
    GET_FOLLOWERS:               (location: string) => `/fabcut/cutting/followers/${location}`,
    GET_ASSIGNED_JOBS:           (location: string) => `/fabcut/cutting/jobs/fetch/${location}`,
    GET_FOLLOWER_JOB:            (deviceId: string) => `/fabcut/cutting/jobs/${deviceId}`,
    DELETE_FOLLOWER_JOB:         (jobId: string) =>    `/fabcut/cutting/jobs/${jobId}`,
    ASSIGN_JOB:                  (deviceId: string) => `/fabcut/cutting/jobs/${deviceId}`,

    // End-bit (leader side — assign / view / delete)
    GET_FOLLOWER_END_BIT_JOB:    (deviceId: string) =>     `/fabcut/cutting/end-bits/jobs/${deviceId}`,
    DELETE_FOLLOWER_END_BIT_JOB: (endBitJobId: string) =>  `/fabcut/cutting/end-bits/jobs/${endBitJobId}`,
    ASSIGN_END_BIT_JOB:          (deviceId: string) =>     `/fabcut/cutting/end-bits/jobs/${deviceId}`,
    GET_LAY_NUMBERS:             (markerId: string) =>     `/fabcut/cutting/end-bits/lay-numbers/${markerId}`,
  },

  // ─── Follower ─────────────────────────────────────────────────────────────
  FOLLOWER: {
    // Normal job
    GET_JOB:         (deviceId: string) =>                  `/fabcut/cutting/jobs/${deviceId}`,
    COMPLETE_JOB:    (jobId: string) =>                     `/fabcut/cutting/jobs/${jobId}`,
    SCAN_BARCODE:    (jobId: string, barcode: string) =>    `/fabcut/cutting/jobs/${jobId}/barcodes/${barcode}`,
    REMOVE_BARCODE:  (jobId: string, barcode: string) =>    `/fabcut/cutting/jobs/${jobId}/barcodes/${barcode}`,
    VALIDATE_PLIES:  (jobId: string, barcode: string) =>    `/fabcut/cutting/jobs/${jobId}/barcodes/${barcode}`,

    // End-bit job (follower side — scan / validate / delete / complete)
    GET_END_BIT_JOB:           (deviceId: string) =>                 `/fabcut/cutting/end-bits/jobs/${deviceId}`,
    COMPLETE_END_BIT_JOB:      (jobId: string) =>                    `/fabcut/cutting/end-bits/jobs/${jobId}`,
    SCAN_END_BIT_BARCODE:      (jobId: string, barcode: string) =>   `/fabcut/cutting/end-bits/jobs/${jobId}/barcodes/${barcode}`,
    REMOVE_END_BIT_BARCODE:    (jobId: string, barcode: string) =>   `/fabcut/cutting/end-bits/jobs/${jobId}/barcodes/${barcode}`,
    VALIDATE_END_BIT_PLIES:    (jobId: string, barcode: string) =>   `/fabcut/cutting/end-bits/jobs/${jobId}/barcodes/${barcode}`,
  },

  // ─── Parts (BundlePartsScreen + EditBundleModal) ──────────────────────────
  PARTS: {
    GET_PARTS:     (ocNo: string) => `/fabcut/cutting/parts/${ocNo}`,
    UPDATE_PARTS:  (ocNo: string) => `/fabcut/cutting/parts/${ocNo}`,
    GET_COUNTRIES: (ocNo: string) => `/fabcut/cutting/parts/${ocNo}/country`,
    ADD_BARCODES:  (ocNo: string) => `/fabcut/cutting/parts/barcode/${ocNo}`,
  },

} as const;
