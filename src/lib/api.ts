import { apiClient } from './client';
import { ENDPOINTS } from './endpoints';
import type { IsDevRegResponse, RegisterRequest, UnregisterRequest } from '../types/auth';
import type {
  OcItemCode, MarkerResponse, JobDetailsResponse, Follower, JobListItem,
  AssignJobRequest, AssignEndBitJobRequest,
  FetchPartsResponse, UpdateBundlePartsRequest, AddBarcodesRequest,
} from '../types/leader';
import type {
  FetchFollowerJobResponse, FetchFollowerEndBitJobResponse,
  ScanBarcodeRequest, ScanBarcodeResponse,
  EndBitJobRequest, ValidateActualPliesRequest,
} from '../types/follower';

// ─── Location ─────────────────────────────────────────────────────────────────
export const validateLocation = (params: { latitude: number; longitude: number }) =>
  apiClient.get<string[]>(ENDPOINTS.LOCATION, { params });

// ─── Device Registration ──────────────────────────────────────────────────────
export const checkDeviceRegistration = (deviceId: string, location: string) =>
  apiClient.get<IsDevRegResponse>(ENDPOINTS.REGISTER_DEVICE.CHECK(deviceId), {
    params: { location },
  });

export const registerDevice = (deviceId: string, data: RegisterRequest) =>
  apiClient.post<boolean>(ENDPOINTS.REGISTER_DEVICE.REGISTER(deviceId), data);

export const unregisterDevice = (deviceId: string, data: UnregisterRequest) =>
  apiClient.delete<boolean>(ENDPOINTS.REGISTER_DEVICE.UNREGISTER(deviceId), { data });

// ─── Leader — OC / Ratios / Markers ──────────────────────────────────────────
// BundlePartsScreen only — NOT used for OC validation on LeaderScreen
export const validateOcAndFetchItemCodes = (ocNo: string, location: string) =>
  apiClient.get<OcItemCode[]>(ENDPOINTS.LEADER.VALIDATE_OC_AND_FETCH_ITEMS(ocNo), {
    params: { location },
  });

// LeaderScreen OC blur — validates OC + returns fit types
export const fetchFitTypes = (ocNo: string, location: string) =>
  apiClient.get<string[]>(ENDPOINTS.LEADER.GET_FIT_TYPES(ocNo), {
    params: { location },
  });

// LeaderScreen Submit — load marker list
export const fetchMarkers = (ocNo: string, location: string, fitType: string) =>
  apiClient.get<MarkerResponse[]>(ENDPOINTS.LEADER.GET_MARKERS(ocNo), {
    params: { location, fitType },
  });

// LeaderJobsScreen on mount — load ratio/size details
export const fetchJobDetails = (marker: string) =>
  apiClient.get<JobDetailsResponse>(ENDPOINTS.LEADER.GET_JOB_DETAILS(marker));

// ─── Leader — Followers ───────────────────────────────────────────────────────
export const fetchFollowers = (location: string) =>
  apiClient.get<Follower[]>(ENDPOINTS.LEADER.GET_FOLLOWERS(location));

export const fetchFollowerJob = (deviceId: string, location: string) =>
  apiClient.get<FetchFollowerJobResponse>(ENDPOINTS.LEADER.GET_FOLLOWER_JOB(deviceId), {
    params: { location },
  });

export const fetchFollowerEndBitJob = (deviceId: string, location: string) =>
  apiClient.get<FetchFollowerEndBitJobResponse>(ENDPOINTS.LEADER.GET_FOLLOWER_END_BIT_JOB(deviceId), {
    params: { location },
  });

// ─── Leader — Assign Jobs ─────────────────────────────────────────────────────
export const assignJobToFollower = (deviceId: string, data: AssignJobRequest) =>
  apiClient.post<number>(ENDPOINTS.LEADER.ASSIGN_JOB(deviceId), data);

export const assignEndBitJobToFollower = (deviceId: string, data: AssignEndBitJobRequest) =>
  apiClient.post<boolean>(ENDPOINTS.LEADER.ASSIGN_END_BIT_JOB(deviceId), data);

// ─── Leader — Delete Jobs ─────────────────────────────────────────────────────
export const deleteFollowerJob = (jobId: string) =>
  apiClient.delete<boolean>(ENDPOINTS.LEADER.DELETE_FOLLOWER_JOB(jobId));

export const deleteFollowerEndBitJob = (endBitJobId: string, data: EndBitJobRequest) =>
  apiClient.delete<boolean>(ENDPOINTS.LEADER.DELETE_FOLLOWER_END_BIT_JOB(endBitJobId), { data });

// ─── Leader — Assigned Jobs List ──────────────────────────────────────────────
export const fetchAssignedJobs = (location: string) =>
  apiClient.get<JobListItem[]>(ENDPOINTS.LEADER.GET_ASSIGNED_JOBS(location));

// ─── Leader — End-bit Lay Numbers ─────────────────────────────────────────────
export const fetchLayNumbers = (markerId: string, itemCode: string, location: string) =>
  apiClient.get<number[]>(ENDPOINTS.LEADER.GET_LAY_NUMBERS(markerId), {
    params: { itemCode, location },
  });

// ─── Follower — Normal Job ────────────────────────────────────────────────────
export const getMyJob = (deviceId: string, location: string) =>
  apiClient.get<FetchFollowerJobResponse>(ENDPOINTS.FOLLOWER.GET_JOB(deviceId), {
    params: { location },
  });

export const completeJob = (jobId: string) =>
  apiClient.put<boolean>(ENDPOINTS.FOLLOWER.COMPLETE_JOB(jobId));

export const scanBarcode = (jobId: string, barcode: string, data: ScanBarcodeRequest) =>
  apiClient.post<ScanBarcodeResponse>(ENDPOINTS.FOLLOWER.SCAN_BARCODE(jobId, barcode), data);

export const removeBarcode = (jobId: string, barcode: string) =>
  apiClient.delete<boolean>(ENDPOINTS.FOLLOWER.REMOVE_BARCODE(jobId, barcode));

export const validateActualPlies = (jobId: string, barcode: string, data: ValidateActualPliesRequest) =>
  apiClient.put<boolean>(ENDPOINTS.FOLLOWER.VALIDATE_PLIES(jobId, barcode), data);

// ─── Follower — End-bit Job ───────────────────────────────────────────────────
export const getMyEndBitJob = (deviceId: string, location: string) =>
  apiClient.get<FetchFollowerEndBitJobResponse>(ENDPOINTS.FOLLOWER.GET_END_BIT_JOB(deviceId), {
    params: { location },
  });

export const completeEndBitJob = (jobId: string, data: EndBitJobRequest) =>
  apiClient.put<boolean>(ENDPOINTS.FOLLOWER.COMPLETE_END_BIT_JOB(jobId), data);

export const scanEndBitBarcode = (
  jobId: string, barcode: string, partName: string, data: EndBitJobRequest,
) =>
  apiClient.post<ScanBarcodeResponse>(
    ENDPOINTS.FOLLOWER.SCAN_END_BIT_BARCODE(jobId, barcode),
    data,
    { params: { partName } },
  );

export const removeEndBitBarcode = (
  jobId: string, barcode: string, partName: string, data: EndBitJobRequest,
) =>
  apiClient.delete<boolean>(ENDPOINTS.FOLLOWER.REMOVE_END_BIT_BARCODE(jobId, barcode), {
    data,
    params: { partName },
  });

export const validateEndBitActualPlies = (
  jobId: string, barcode: string, partName: string, data: ValidateActualPliesRequest,
) =>
  apiClient.put<boolean>(ENDPOINTS.FOLLOWER.VALIDATE_END_BIT_PLIES(jobId, barcode), data, {
    params: { partName },
  });

// ─── Parts (BundlePartsScreen + EditBundleModal) ──────────────────────────────
export const fetchPartsForOC = (ocNo: string, location: string) =>
  apiClient.get<FetchPartsResponse>(ENDPOINTS.PARTS.GET_PARTS(ocNo), {
    params: { location },
  });

export const updateSelectedBundleParts = (ocNo: string, data: UpdateBundlePartsRequest) =>
  apiClient.put<boolean>(ENDPOINTS.PARTS.UPDATE_PARTS(ocNo), data);

export const addBarcodesForOC = (ocNo: string, data: AddBarcodesRequest) =>
  apiClient.post<boolean>(ENDPOINTS.PARTS.ADD_BARCODES(ocNo), data);

export const fetchCountries = (ocNo: string) =>
  apiClient.get<string[]>(ENDPOINTS.PARTS.GET_COUNTRIES(ocNo));
        