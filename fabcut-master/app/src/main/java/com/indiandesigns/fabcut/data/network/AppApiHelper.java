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
import com.rx2androidnetworking.Rx2AndroidNetworking;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

/**
 * Manages API calls to server
 */

@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiHeader mApiHeader;

    /**
     * Parameterized Constructor
     * <p>
     * Instantiates ApiHeader
     *
     * @param apiHeader Injected with Dagger
     */

    @Inject
    public AppApiHelper(ApiHeader apiHeader) {
        mApiHeader = apiHeader;
    }

    /**
     * Retrieves the API Header
     *
     * @return Instance of ApiHeader
     */

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    /**
     * Performs the Is Device Registered API Call
     *
     * @param request {@link IsDevRegRequest} with desired values
     * @param deviceId Device Id
     * @return Single observable of IsDevRegResponse
     */

    @Override
    public Single<IsDevRegResponse> doIsDeviceRegisteredCall(String deviceId, IsDevRegRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.REGISTRATIONS+"/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectSingle(IsDevRegResponse.class);
    }

    /**
     * Performs the Register Device API Call
     *
     * @param request {@link RegDevRequest} with desired values
     * @param deviceId Device Id
     * @return Single observable of Boolean
     */

    @Override
    public Single<Boolean> doRegisterDeviceCall(String deviceId, RegDevRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.REGISTRATIONS+"/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * Performs the Unregister Device API Call
     *
     * @param request {@link UnregisterDevRequest} with desired values
     * @param deviceId Device Id
     * @return Single observable of Boolean
     */

    @Override
    public Single<Boolean> doUnregisterDeviceCall(String deviceId, UnregisterDevRequest request) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.REGISTRATIONS+"/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * Performs the Validate Location API call
     *
     * @param request Request with desired values
     * @return Single observable of list of locations
     */

    @Override
    public Single<List<String>> doValidateLocationCall(LocRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.VALIDATE_LOCATION)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectListSingle(String.class);
    }


    /**
     * Performs the fetch job details API call. This fetches all the ratios associated
     * with a specific marker
     *
     * @param marker Marker
     * @return Single observable of job detail
     */
    public Single<JobDetails> doFetchJobDetailsCall(String marker) {
        return Rx2AndroidNetworking.get(ApiEndPoint.RATIOS+"/"+marker)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(JobDetails.class);
    }

    /**
     * API request to validate the OC# and fetch all item codes associated with that OC#
     * @param request {@link ValidateOcAndFetchItemsRequest}
     * @param ocNo OC Number
     * @return Single observable of List of ValidateOcAndFetchItemsData
     */
    public Single<List<ValidateOcAndFetchItemsData>> doValidateOcNoAndFetchItemCode(String ocNo, ValidateOcAndFetchItemsRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.RATIOS+"/"+ocNo+"/itemCodes")
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectListSingle(ValidateOcAndFetchItemsData.class);
    }

    /**
     * API request to fetch all the followers associated with a specific location
     * @param location Location
     * @return Single observable of List of Followers
     */
    public Single<List<Follower>> doFetchFollowerDeviceIDsCall(String location) {
        return Rx2AndroidNetworking.get(ApiEndPoint.FOLLOWERS+"/"+location)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectListSingle(Follower.class);
    }

    /**
     * API request to assign job to a specific follower
     * @param request {@link AssignJobRequest}
     * @param deviceId Device Id
     * @return Single observable of Boolean
     */

    @Override
    public Single<Integer> doAssignJobToFollowerCall(String deviceId, AssignJobRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.JOBS+"/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Integer.class);
    }

    /**
     * API request to assign an end bit job to a specific follower
     * @param request {@link AssignEndBitJobRequest}
     * @param deviceId Device Id
     * @return Single observable of Boolean
     */
    @Override
    public Single<Boolean> doAssignEndBitJobToFollowerCall(String deviceId, AssignEndBitJobRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.END_BITS+"/jobs/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Boolean.class);
    }
    /**
     * API request to fetch the follower's job details
     * @param request {@link FetchFollowerRequest}
     * @param deviceId Device Id
     * @return {@link FetchFollowerResponse}
     */

    @Override
    public Single<FetchFollowerResponse> doFetchFollowerJobCall(String deviceId, FetchFollowerRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.JOBS+"/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectSingle(FetchFollowerResponse.class);
    }

    /**
     * API request to delete the assigned job from a follower
     * @param jobId Job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doDeleteFollowerJobCall(Integer jobId) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.JOBS+"/"+jobId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to process the scanned barcode against a job id
     * @param jobId Job Id
     * @param barcode Barcode scanned
     * @return {@link ScanBarcodeResponse}
     */

    @Override
    public Single<ScanBarcodeResponse> doScanBarcodeCall(Integer jobId, String barcode, ScanBarcodeRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.JOBS+"/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(ScanBarcodeResponse.class);
    }


    /**
     * API request to process the scanned barcode against an end bit job id
     * @param jobId End bit job Id
     * @param barcode Barcode scanned
     * @param partName The part name under which the barcode was scanned
     * @return {@link ScanBarcodeResponse}
     */

    @Override
    public Single<ScanBarcodeResponse> doScanBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.END_BITS+"/jobs/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter("partName", partName)
                .addBodyParameter(request)
                .build()
                .getObjectSingle(ScanBarcodeResponse.class);    }

    /**
     * API request to complete a job for a follower
     * @param jobId Job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doCompleteFollowerJobCall(Integer jobId) {
        return Rx2AndroidNetworking.put(ApiEndPoint.JOBS+"/"+jobId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to complete an end bit job for a follower
     * @param jobId End bit job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doCompleteFollowerEndBitJobCall(Integer jobId, EndBitJobRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.END_BITS+"/jobs/"+jobId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(Boolean.class);    }

    /**
     * API request to remove a scanned barcode for a follower's job
     * @param jobId Job Id
     * @param barcode Barcode Scanned
     * @return boolean
     */

    @Override
    public Single<Boolean> doRemoveBarcodeCall(Integer jobId, String barcode) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.JOBS+"/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(Boolean.class);
    }


    /**
     * API request to remove a scanned barcode for a follower's end bit job
     * @param jobId End bit job Id
     * @param barcode Barcode Scanned
     * @param partName The part name under which the barcode was scanned
     * @return boolean
     */

    @Override
    public Single<Boolean> doRemoveBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.END_BITS+"/jobs/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter("partName", partName)
                .addBodyParameter(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to validate actual plies for a job
     * @param jobId Job Id
     * @param barcode Barcode scanned
     * @param request request {@link ValidateActualPliesRequest}
     * @return boolean
     */

    @Override
    public Single<Boolean> doValidateActualPliesCall(Integer jobId, String barcode, ValidateActualPliesRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.JOBS+"/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to validate actual plies for an end bit job
     * @param jobId End bit job Id
     * @param barcode Barcode scanned
     * @param partName The part name under which the barcode was scanned
     * @param request request {@link ValidateActualPliesRequest}
     * @return boolean
     */

    @Override
    public Single<Boolean> doValidateActualPliesEndBitCall(Integer jobId, String barcode, String partName, ValidateActualPliesRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.END_BITS+"/jobs/"+jobId+"/barcodes/"+barcode)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter("partName", partName)
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Boolean.class);    }

    /**
     * API request to fetch markers
     * @param ocNo Oc Number entered
     * @param request request {@link FetchMarkersRequest}
     * @return Single observable of List of Marker
     */

    @Override
    public Single<List<Marker>> doFetchMarkersCall(String ocNo, FetchMarkersRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.RATIOS+"/markers/"+ocNo)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectListSingle(Marker.class);
    }

    /**
     * API request to fetch the assigned jobs
     * for a location
     * @param location Location
     * @return List of assigned jobs {@link JobListResponse}
     */

    @Override
    public Single<List<JobListResponse>> doFetchJobsCall(String location) {
        return Rx2AndroidNetworking.get(ApiEndPoint.JOBS+"/fetch/"+location)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectListSingle(JobListResponse.class);
    }

    /**
     * API request to fetch parts for a OC Number
     * @param ocNo Oc Number to fetch parts
     * @param request Location
     * @return Parts information
     */

    @Override
    public Single<FetchPartsResponse> doFetchPartsForOCCall(String ocNo, LocationRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.PARTS+"/"+ocNo)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectSingle(FetchPartsResponse.class);
    }

    /**
     * API request to submit barcodes for a OC Number
     * @param ocNo Oc Number
     * @param request Generated barcode data
     * @return Server response
     */

    @Override
    public Single<Boolean> doAddBarcodesForOCCall(String ocNo, AddPartStickersRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.PARTS+"/barcode/"+ocNo)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to fetch fit types for an oc number
     * @param ocNo Oc Number entered
     * @param request request params {@link FetchFitTypesRequest}
     * @return List of fit types
     */

    @Override
    public Single<List<String>> doFetchFitTypesCall(String ocNo, FetchFitTypesRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.RATIOS+"/"+ocNo+"/"+"fitTypes")
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectListSingle(String.class);
    }

    /**
     * API request to fetch lay numbers based on mareker id, item code and location
     * @param markerId The marker id
     * @param request request param {@link FetchLayNumbersRequest}
     * @return List of lay numbers
     */
    @Override
    public Single<List<Number>> doFetchLayNumbersCall(String markerId, FetchLayNumbersRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.END_BITS+"/lay-numbers/"+markerId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectListSingle(Number.class);

    }

    /**
     * API request to fetch end bit job details assigned to follower
     * @param request {@link FetchFollowerRequest}
     * @param deviceId The device id
     * @return {@link FetchFollowerEndBitJobResponse}
     */
    @Override
    public Single<FetchFollowerEndBitJobResponse> doFetchFollowerEndBitJobCall(String deviceId, FetchFollowerRequest request){
        return Rx2AndroidNetworking.get(ApiEndPoint.END_BITS+"/jobs/"+deviceId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectSingle(FetchFollowerEndBitJobResponse.class);
    }

    /**
     * API request to delete the assigned end bit job from follower
     * @param endBitJobId The end bit job id
     * @param request The request {@link EndBitJobRequest}
     * @return boolean
     */
    @Override
    public Single<Boolean> doDeleteFollowerEndBitJobCall(Integer endBitJobId, EndBitJobRequest request){
        return Rx2AndroidNetworking.delete(ApiEndPoint.END_BITS+"/jobs/"+endBitJobId)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(Boolean.class);
    }

    /**
     * API request to fetch countries for an oc number
     * @param ocNo Oc Number entered
     * @return List of countries
     */
    @Override
    public Single<List<String>> doFetchCountriesCall(String ocNo) {
        return Rx2AndroidNetworking.get(ApiEndPoint.PARTS+"/"+ocNo+"/"+"country")
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectListSingle(String.class);
    }

    /**
     * API request to update selected bundle parts
     * @param ocNo Oc Number entered
     * @param request The request {@link PutPartsDetailsRequest}
     * @return boolean
     */
    @Override
    public Single<Boolean> doPutSelectedBundleParts(String ocNo, PutPartsDetailsRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.PARTS+"/"+ocNo)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .addApplicationJsonBody(request)
                .build()
                .getObjectSingle(Boolean.class);
    }
}

