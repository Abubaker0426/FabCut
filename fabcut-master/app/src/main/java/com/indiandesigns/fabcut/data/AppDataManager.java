package com.indiandesigns.fabcut.data;

import android.content.Context;

import com.indiandesigns.fabcut.data.db.DbHelper;
import com.indiandesigns.fabcut.data.network.ApiHeader;
import com.indiandesigns.fabcut.data.network.ApiHelper;
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
import com.indiandesigns.fabcut.data.network.model.IsDevRegRequest;
import com.indiandesigns.fabcut.data.network.model.IsDevRegResponse;
import com.indiandesigns.fabcut.data.network.model.ItemQuantityData;
import com.indiandesigns.fabcut.data.network.model.JobDetails;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.data.network.model.LocRequest;
import com.indiandesigns.fabcut.data.network.model.LocationRequest;
import com.indiandesigns.fabcut.data.network.model.Marker;
import com.indiandesigns.fabcut.data.network.model.PutPartsDetailsRequest;
import com.indiandesigns.fabcut.data.network.model.RegDevRequest;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeRequest;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeResponse;
import com.indiandesigns.fabcut.data.network.model.UnregisterDevRequest;
import com.indiandesigns.fabcut.data.network.model.ValidateActualPliesRequest;
import com.indiandesigns.fabcut.data.network.model.ValidateOcAndFetchItemsData;
import com.indiandesigns.fabcut.data.network.model.ValidateOcAndFetchItemsRequest;
import com.indiandesigns.fabcut.data.prefs.PreferencesHelper;
import com.indiandesigns.fabcut.di.ApplicationContext;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Performs all data operations
 * <p>
 * We will be using this as the wrapper over all the data operations
 */

@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;

    /**
     * Parameterized Constructor
     * <p>
     * Instantiates Context, {@link PreferencesHelper} and {@link ApiHelper}
     *
     * @param context           Injected with Dagger
     * @param preferencesHelper Injected with Dagger
     * @param apiHelper         Injected with Dagger
     */

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          DbHelper dbHelper,
                          ApiHelper apiHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
        mDbHelper = dbHelper;
    }

    /**
     * Calls the getApiHeader of {@link ApiHelper} to fetch the API Header
     *
     * @return API Header
     */

    @Override
    public ApiHeader getApiHeader() {
        return mApiHelper.getApiHeader();
    }

    /**
     * Make the doIsDeviceRegisteredCall API call as a GET request
     * <p>
     * Calls the doIsDeviceRegisteredCall of {@link ApiHelper} with desired request parameters
     * Also, sets the authentication as a request query
     *
     * @param request Desired API parameters
     * @param deviceId Device Id
     * @return Single observable of ApiResponse
     */

    @Override
    public Single<IsDevRegResponse> doIsDeviceRegisteredCall(String deviceId, IsDevRegRequest request) {
        return mApiHelper.doIsDeviceRegisteredCall(deviceId, request);
    }

    /**
     * Make the doRegisterDeviceCall API call
     * <p>
     * Calls the doRegisterDeviceCall of {@link ApiHelper} with desired request parameters
     *
     * @param request Desired API parameters
     * @param deviceId Device Id
     * @return Single observable of ApiResponse
     */

    @Override
    public Single<Boolean> doRegisterDeviceCall(String deviceId, RegDevRequest request) {
        return mApiHelper.doRegisterDeviceCall(deviceId, request);
    }

    /**
     * Make the doUnregisterDeviceCall API call
     * <p>
     * Calls the doUnregisterDeviceCall of {@link ApiHelper} with desired request parameters
     *
     * @param request Desired API parameters
     * @param deviceId Device Id
     * @return Single observable of ApiResponse
     */

    @Override
    public Single<Boolean> doUnregisterDeviceCall(String deviceId, UnregisterDevRequest request) {
        return mApiHelper.doUnregisterDeviceCall(deviceId, request);
    }

    /**
     * Make the doValidateLocationCall API call
     *
     * Calls the doValidateLocationCall of {@link ApiHelper} with desired request parameters
     *
     * @param request Desired API parameters
     * @return JSONArray
     */

    @Override
    public Single<List<String>> doValidateLocationCall(LocRequest request) {
        return mApiHelper.doValidateLocationCall(request);
    }

    /**
     * Calls the doFetchJobDetailsCall of {@link ApiHelper}
     * @param marker Marker
     * @return {@link JobDetails}
     */
    public Single<JobDetails> doFetchJobDetailsCall(String marker) {
        return mApiHelper.doFetchJobDetailsCall(marker);
    }

    /**
     * Makes the doValidateOcNoAndFetchItemCode call
     * @param request {@link ValidateOcAndFetchItemsRequest}
     * @param ocNo OC Number
     * @return
     */
    public Single<List<ValidateOcAndFetchItemsData>> doValidateOcNoAndFetchItemCode(String ocNo, ValidateOcAndFetchItemsRequest request) {
        return mApiHelper.doValidateOcNoAndFetchItemCode(ocNo, request);
    }

    /**
     * Make the doFetchFollowerDeviceIds call
     * @param location Location
     * @return
     */
    public Single<List<Follower>> doFetchFollowerDeviceIDsCall(String location) {
        return mApiHelper.doFetchFollowerDeviceIDsCall(location);
    }

    /**
     * Make the doFetchMarkersCall call
     * @param ocNo Oc Number
     * @param request {@link FetchMarkersRequest}
     * @return Single observable of List of Marker
     */

    @Override
    public Single<List<Marker>> doFetchMarkersCall(String ocNo, FetchMarkersRequest request) {
        return mApiHelper.doFetchMarkersCall(ocNo, request);
    }

    /**
     * Make the doFetchFitTypesCall call
     * @param ocNo Oc Number
     * @param request request params {@link FetchFitTypesRequest}
     * @return List of fit types
     */

    @Override
    public Single<List<String>> doFetchFitTypesCall(String ocNo, FetchFitTypesRequest request) {
        return mApiHelper.doFetchFitTypesCall(ocNo, request);
    }

    /**
     * Make the doFetchLayNumbersCall call
     * @param markerId The marker id
     * @param request request params {@link FetchLayNumbersRequest}
     * @return
     */

    @Override
    public Single<List<Number>> doFetchLayNumbersCall(String markerId, FetchLayNumbersRequest request) {
        return mApiHelper.doFetchLayNumbersCall(markerId, request);
    }

    /**
     * Make the doFetchFollowerEndBitJobCall call
     * @param deviceId The device id
     * @param request request params {@link FetchFollowerRequest}
     * @return {@link FetchFollowerEndBitJobResponse}
     */

    @Override
    public Single<FetchFollowerEndBitJobResponse> doFetchFollowerEndBitJobCall(String deviceId, FetchFollowerRequest request) {
        return mApiHelper.doFetchFollowerEndBitJobCall(deviceId, request);
    }

    /**
     * Make the doDeleteFollowerEndBitJobCall call
     * @param endBitJobId The end bit job id
     * @param request request params {@link EndBitJobRequest}
     * @return boolean
     */

    @Override
    public Single<Boolean> doDeleteFollowerEndBitJobCall(Integer endBitJobId, EndBitJobRequest request) {
        return mApiHelper.doDeleteFollowerEndBitJobCall(endBitJobId, request);
    }

    /**
     * Make the doFetchCountriesCall call
     * @param ocNo Oc Number
     * @return List of countries
     */

    @Override
    public Single<List<String>> doFetchCountriesCall(String ocNo) {
        return mApiHelper.doFetchCountriesCall(ocNo);
    }

    /**
     * Make the doFetchCountriesCall call
     * @param ocNo Oc Number
     * @param request {@link PutPartsDetailsRequest}
     * @return Single observable of Boolean
     */
    @Override
    public Single<Boolean> doPutSelectedBundleParts(String ocNo, PutPartsDetailsRequest request) {
        return mApiHelper.doPutSelectedBundleParts(ocNo, request);
    }

    /**
     * Make the doAssignJobToFollowerCall call
     * @param request {@link AssignJobRequest}
     * @param deviceId Device Id
     * @return Single observable of Boolean
     */

    @Override
    public Single<Integer> doAssignJobToFollowerCall(String deviceId, AssignJobRequest request) {
        return mApiHelper.doAssignJobToFollowerCall(deviceId, request);
    }

    /**
     * Make the doAssignEndBitJobToFollower call
     * @param  request {@link AssignEndBitJobRequest}
     * @param deviceId Device id
     * @return Single observable of Boolean
     */

    @Override
    public Single<Boolean> doAssignEndBitJobToFollowerCall(String deviceId, AssignEndBitJobRequest request){
        return mApiHelper.doAssignEndBitJobToFollowerCall(deviceId, request);
    }

    /**
     * Calls the setAccessToken of @{@link ApiHeader.ProtectedApiHeader} to update the Access Token
     *
     * @param accessToken New Access Token value
     */

    @Override
    public void updateApiHeader(String accessToken) {
        mApiHelper.getApiHeader().getProtectedApiHeader().setAccessToken(accessToken);
    }

    /**
     * Fetch the current access token
     * <p>
     * Calls the getAccessToken of @{@link PreferencesHelper}
     * to fetch the current saved Access Token
     *
     * @return String value of API Header
     */

    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public String getRefreshToken() {
        return mPreferencesHelper.getRefreshToken();
    }

    /**
     * Update the current value of access token
     * <p>
     * Calls the setAccessToken of @{@link PreferencesHelper}
     * to update the current saved Access Token
     * <p>
     * Updates the API Header with the new Access Token
     */

    @Override
    public void setAccessToken(String accessToken) {
        mPreferencesHelper.setAccessToken(accessToken);
        mApiHelper.getApiHeader().getProtectedApiHeader().setAccessToken(accessToken);
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        mPreferencesHelper.setRefreshToken(refreshToken);
    }

    /**
     * Update the current location set for the device
     * <p>
     *     Calls the setLocation of {@link com.indiandesigns.fabcut.data.prefs.AppPreferencesHelper}
     *     to update the location
     * </p>
     * @param location
     */
    public void setLocation(String location) {
        mPreferencesHelper.setLocation(location);
    }

    /**
     * Fetch the current location
     * @return String current location
     */
    public String getLocation() {
        return mPreferencesHelper.getLocation();
    }

    /**
     * Set the current role for the device
     * <p>
     *     Calls the setRole of {@link com.indiandesigns.fabcut.data.prefs.AppPreferencesHelper}
     *     to set the role
     * </p>
     * @param role
     */
    public void setRole(String role) {
        mPreferencesHelper.setRole(role);
    }

    /**
     * Fetch the current role for the device
     * @return current role of device
     */
    public String getRole() {
        return mPreferencesHelper.getRole();
    }

    /**
     * Remove the current role associated with this device. Called when a device is unregistered
     */
    public void deleteRole() {
        mPreferencesHelper.deleteRole();
    }

    /**
     * Fetch the follower's assigned job
     * @param request {@link FetchFollowerRequest}
     * @param deviceId Device Id
     * @return {@link FetchFollowerResponse}
     */

    @Override
    public Single<FetchFollowerResponse> doFetchFollowerJobCall(String deviceId, FetchFollowerRequest request) {
        return mApiHelper.doFetchFollowerJobCall(deviceId, request);
    }

    /**
     * Delete a job assigned to a follower
     * @param jobId Job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doDeleteFollowerJobCall(Integer jobId) {
        return mApiHelper.doDeleteFollowerJobCall(jobId);
    }

    /**
     * Process a barcode scanned for a job
     * @param jobId Job Id
     * @param barcode Barcode scanned
     * @return {@link ScanBarcodeResponse}
     */

    @Override
    public Single<ScanBarcodeResponse> doScanBarcodeCall(Integer jobId, String barcode, ScanBarcodeRequest request) {
        return mApiHelper.doScanBarcodeCall(jobId, barcode, request);
    }


    /**
     * Process a barcode scanned for an end bit job
     * @param jobId End bit job Id
     * @param barcode Barcode scanned
     * @param partName The part name under which the barcode was scanned
     * @return {@link ScanBarcodeResponse}
     */

    @Override
    public Single<ScanBarcodeResponse> doScanBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request) {
        return mApiHelper.doScanBarcodeEndBitCall(jobId, barcode, partName, request);
    }

    /**
     * Complete a follower's job
     * @param jobId Job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doCompleteFollowerJobCall(Integer jobId) {
        return mApiHelper.doCompleteFollowerJobCall(jobId);
    }

    /**
     * Complete a follower's end bit job
     * @param jobId End bit job Id
     * @return boolean
     */

    @Override
    public Single<Boolean> doCompleteFollowerEndBitJobCall(Integer jobId, EndBitJobRequest request) {
        return mApiHelper.doCompleteFollowerEndBitJobCall(jobId, request);
    }

    /**
     * Remove a scanned barcode for a job
     * @param jobId Job Id
     * @param barcode Scanned Barcode
     * @return boolean
     */

    @Override
    public Single<Boolean> doRemoveBarcodeCall(Integer jobId, String barcode) {
        return mApiHelper.doRemoveBarcodeCall(jobId, barcode);
    }

    /**
     * Remove a scanned barcode for an end bit job
     * @param jobId End bit job Id
     * @param barcode Scanned Barcode
     * @param partName The part name under which the barcode was scanned
     * @return boolean
     */

    @Override
    public Single<Boolean> doRemoveBarcodeEndBitCall(Integer jobId, String barcode, String partName, EndBitJobRequest request) {
        return mApiHelper.doRemoveBarcodeEndBitCall(jobId, barcode, partName, request);
    }

    /**
     * Validate actual plies for a job
     * @param request {@link ValidateActualPliesRequest}
     * @param jobId Job Id
     * @param barcode Scanned Barcode
     * @return boolean
     */

    @Override
    public Single<Boolean> doValidateActualPliesCall(Integer jobId, String barcode, ValidateActualPliesRequest request) {
        return mApiHelper.doValidateActualPliesCall(jobId, barcode, request);
    }

    /**
     * Validate actual plies for an end bit job
     * @param request {@link ValidateActualPliesRequest}
     * @param jobId End bit job Id
     * @param barcode Scanned Barcode
     * @param partName The part name under which the barcode was scanned
     * @return boolean
     */

    @Override
    public Single<Boolean> doValidateActualPliesEndBitCall(Integer jobId, String barcode, String partName, ValidateActualPliesRequest request) {
        return mApiHelper.doValidateActualPliesEndBitCall(jobId, barcode, partName, request);
    }

    /**
     * To insert a scan barcode into the database
     *
     * Calls the saveScanBarcode of {@link DbHelper} to insert a new scan barcode in barcodes table
     *
     * @param barcode The scan barcode to be inserted
     * @return Observable of true if barcode was inserted into the table
     */

    @Override
    public Observable<Long> saveScanBarcode(ScanBarcode barcode) {
        return mDbHelper.saveScanBarcode(barcode);
    }

    /**
     * To fetch the scan barcodes from database for a specific job session
     *
     * Calls the getScanBarcodes of {@link DbHelper} to fetch the scan barcodes
     *
     * @return Observable of List of scan barcodes for a specific session
     */

    @Override
    public Observable<List<ScanBarcode>> getScanBarcodes(int jobId) {
        return mDbHelper.getScanBarcodes(jobId);
    }

    /**
     * To fetch the scan barcodes from database for a specific job session and item code
     *
     * Calls the getScanBarcodes of {@link DbHelper} to fetch the scan barcodes
     *
     * @return Observable of List of scan barcodes for a specific session
     */

    @Override
    public Observable<List<ScanBarcode>> getScanBarcodes(int jobId, String itemCode) {
        return mDbHelper.getScanBarcodes(jobId, itemCode);
    }

    /**
     * To delete the barcodes from database for a specific job session
     *
     * Calls the deleteScanBarcodes of {@link DbHelper} to delete the scan barcodes
     *
     * @return Observable of true if scan barcodes were deleted
     */

    @Override
    public Observable<Boolean> deleteScanBarcodes(int jobId) {
        return mDbHelper.deleteScanBarcodes(jobId);
    }

    /**
     * To delete a specific barcode from database
     *
     * Calls the deleteScanBarcode of {@link DbHelper} to delete the scan barcode
     *
     * @return Observable of true if scan barcode was deleted
     */

    @Override
    public Observable<Boolean> deleteScanBarcode(ScanBarcode barcode) {
        return mDbHelper.deleteScanBarcode(barcode);
    }

    /**
     * To update a specific barcode in database
     *
     * Calls the updateScanBarcode of {@link DbHelper} to update the scan barcode
     *
     * @return Observable of true if scan barcode was updated
     */

    @Override
    public Observable<Boolean> updateScanBarcode(ScanBarcode barcode) {
        return mDbHelper.updateScanBarcode(barcode);
    }

    /**
     * Make the doFetchJobsCall call
     * @param location Location
     * @return Single observable of List of Jobs
     */

    @Override
    public Single<List<JobListResponse>> doFetchJobsCall(String location) {
        return mApiHelper.doFetchJobsCall(location);
    }

    /**
     * Make the doFetchPartsForOCCall call
     * @param ocNo Oc Number
     * @param request {@link LocationRequest}
     * @return Single observable of Parts data
     */

    @Override
    public Single<FetchPartsResponse> doFetchPartsForOCCall(String ocNo, LocationRequest request) {
        return mApiHelper.doFetchPartsForOCCall(ocNo, request);
    }

    /**
     * Make the doAddBarcodesForOCCall call
     * @param ocNo Oc Number
     * @param request {@link AddPartStickersRequest}
     * @return Single observable of server response
     */

    @Override
    public Single<Boolean> doAddBarcodesForOCCall(String ocNo, AddPartStickersRequest request) {
        return mApiHelper.doAddBarcodesForOCCall(ocNo, request);
    }

    /**
     * Fetches all quantity data for all current item codes
     * @return Map of quantities data for all current item codes
     */

    @Override
    public HashMap<String, ItemQuantityData> getItemQuantities() {
        return mPreferencesHelper.getItemQuantities();
    }

    /**
     * Sets quantity data for an item code
     * @param itemCode Item code
     * @param quantityData Cut and assigned quantities
     */

    @Override
    public void setItemQuantityData(String itemCode, ItemQuantityData quantityData) {
        mPreferencesHelper.setItemQuantityData(itemCode, quantityData);
    }

    /**
     * Clear quantity data for item codes
     */

    @Override
    public void clearItemQuantityData() {
        mPreferencesHelper.clearItemQuantityData();
    }
}
