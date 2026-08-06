package com.indiandesigns.fabcut.data.network;

import com.indiandesigns.fabcut.data.network.model.AddPartStickersRequest;
import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.EndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFitTypesRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.FetchLayNumbersRequest;
import com.indiandesigns.fabcut.data.network.model.FetchMarkersRequest;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.JobDetails;
import com.indiandesigns.fabcut.data.network.model.IsDevRegRequest;
import com.indiandesigns.fabcut.data.network.model.IsDevRegResponse;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.data.network.model.LocRequest;
import com.indiandesigns.fabcut.data.network.model.LocationRequest;
import com.indiandesigns.fabcut.data.network.model.Marker;
import com.indiandesigns.fabcut.data.network.model.PutPartsDetailsRequest;
import com.indiandesigns.fabcut.data.network.model.RegDevRequest;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeRequest;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeResponse;
import com.indiandesigns.fabcut.data.network.model.UnregisterDevRequest;
import com.indiandesigns.fabcut.data.network.model.ValidateActualPliesRequest;
import com.indiandesigns.fabcut.data.network.model.ValidateOcAndFetchItemsData;
import com.indiandesigns.fabcut.data.network.model.ValidateOcAndFetchItemsRequest;

import java.util.List;

import io.reactivex.Single;

/**
 * Interface for performing API calls and fetching the API Header
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    Single<IsDevRegResponse> doIsDeviceRegisteredCall(String deviceId, IsDevRegRequest request);

    Single<Boolean> doRegisterDeviceCall(String deviceId, RegDevRequest request);

    Single<Boolean> doUnregisterDeviceCall(String deviceId, UnregisterDevRequest request);

    Single<List<String>> doValidateLocationCall(LocRequest request);

    Single<JobDetails> doFetchJobDetailsCall(String marker);

    Single<List<ValidateOcAndFetchItemsData>> doValidateOcNoAndFetchItemCode(String ocNo, ValidateOcAndFetchItemsRequest request);

    Single<List<Follower>> doFetchFollowerDeviceIDsCall(String location);

    Single<Integer> doAssignJobToFollowerCall(String deviceId, AssignJobRequest request);

    Single<Boolean> doAssignEndBitJobToFollowerCall(String deviceId, AssignEndBitJobRequest request);

    Single<FetchFollowerResponse> doFetchFollowerJobCall(String deviceId, FetchFollowerRequest request);

    Single<Boolean> doDeleteFollowerJobCall(Integer jobId);

    Single<ScanBarcodeResponse> doScanBarcodeCall(Integer jobId, String barcode, ScanBarcodeRequest request);

    Single<ScanBarcodeResponse> doScanBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request);

    Single<Boolean> doCompleteFollowerJobCall(Integer jobId);

    Single<Boolean> doCompleteFollowerEndBitJobCall(Integer jobId, EndBitJobRequest request);

    Single<Boolean> doRemoveBarcodeCall(Integer jobId, String barcode);

    Single<Boolean> doRemoveBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request);

    Single<Boolean> doValidateActualPliesCall(Integer jobId, String barcode, ValidateActualPliesRequest request);

    Single<Boolean> doValidateActualPliesEndBitCall(Integer jobId, String barcode, String partName, ValidateActualPliesRequest request);

    Single<List<Marker>> doFetchMarkersCall(String ocNo, FetchMarkersRequest request);

    Single<List<JobListResponse>> doFetchJobsCall(String location);

    Single<FetchPartsResponse> doFetchPartsForOCCall(String ocNo, LocationRequest request);

    Single<Boolean> doAddBarcodesForOCCall(String ocNo, AddPartStickersRequest request);

    Single<List<String>> doFetchFitTypesCall(String ocNo, FetchFitTypesRequest request);

    Single<List<Number>> doFetchLayNumbersCall(String markerId, FetchLayNumbersRequest request);

    Single<FetchFollowerEndBitJobResponse> doFetchFollowerEndBitJobCall(String deviceId, FetchFollowerRequest request);

    Single<Boolean> doDeleteFollowerEndBitJobCall(Integer endBitJobId, EndBitJobRequest request);

    Single<List<String>> doFetchCountriesCall(String ocNo);

    Single<Boolean> doPutSelectedBundleParts(String ocNo, PutPartsDetailsRequest request);
}
