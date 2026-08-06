package com.indiandesigns.fabcut.ui.follower.job_detail;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.EndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse.JobDetail;
import com.indiandesigns.fabcut.data.network.model.ItemQuantityData;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeResponse;
import com.indiandesigns.fabcut.data.network.model.ValidateActualPliesRequest;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class EndBitJobDetailPresenter<V extends EndBitJobDetailMvpView> extends BasePresenter<V>
        implements EditEndBitJobDetailMvpPresenter<V> {

    @Inject
    public EndBitJobDetailPresenter(DataManager dataManager,
                              SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void saveItemQuantityData(Double cut, Double assigned, String itemCode) {
        ItemQuantityData data = new ItemQuantityData(cut, assigned);
        getDataManager().setItemQuantityData(itemCode, data);
    }

    /**
     * Fetch data for a scanned barcode and update UI
     * @param code Scanned code
     * @param jobId Job Id
     * @param partName The part name
     */

    @Override
    public void getBarcodeData(String code, Integer jobId, String partName) {
        if(jobId == null){
            getMvpView().onError(R.string.no_job_assigned);
            return;
        }

        getMvpView().showLoading();

        EndBitJobRequest request = new EndBitJobRequest();
        request.setLocation(getDataManager().getLocation());
        request.setDeviceId(getMvpView().getDeviceId());

        getCompositeDisposable().add(getDataManager()
                .doScanBarcodeEndBitCall(jobId, code, partName, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ScanBarcodeResponse>() {
                    @Override
                    public void accept(ScanBarcodeResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();

                        ScanBarcode barcode = new ScanBarcode();
                        barcode.setBarcode(code);
                        barcode.setExpectedPlies(response.getPlies());
                        barcode.setReason("");
                        barcode.setActualPlies(0.0);
                        barcode.setItemCode(partName);

                        insertBarcode(barcode, jobId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.fetch_barcode_data_error);
                        }
                    }
                }));
    }

    /**
     * Inserts the given barcode in the database
     * @param barcode Barcode to be inserted
     * @param jobId Job Id
     */

    private void insertBarcode(ScanBarcode barcode, Integer jobId) {
        barcode.setJobId(jobId);
        getCompositeDisposable().add(getDataManager()
                .saveScanBarcode(barcode)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long id) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().updateBarcodeList(barcode);
                    }
                }));
    }

    /**
     * Deletes a scanned barcode for a job
     * @param barcode Barcode to delete
     * @param jobId Job Id
     * @param position Position to be remove
     */

    @Override
    public void delete(ScanBarcode barcode, Integer jobId, String partName, int position) {
        getMvpView().showLoading();

        EndBitJobRequest request = new EndBitJobRequest();
        request.setLocation(getDataManager().getLocation());
        request.setDeviceId(getMvpView().getDeviceId());

        getCompositeDisposable().add(getDataManager()
                .doRemoveBarcodeEndBitCall(jobId, barcode.getBarcode(), partName, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        deleteBarcode(barcode);
                        getMvpView().removeBarcodeFromList(position);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.delete_barcode_error);
                        }
                    }
                }));
    }

    /**
     * Deletes a given barcode from the local database
     * @param item Barcode to be deleted
     */

    @Override
    public void deleteBarcode(ScanBarcode item) {
        getCompositeDisposable().add(getDataManager()
                .deleteScanBarcode(item)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                    }
                }));
    }

    /**
     * Fetches the follower's job details as soon as view is prepared
     */

    @Override
    public void onViewPrepared(String ocNumber, Double layLength, Integer jobId, String itemCode, String itemDesc, JobDetail jobDetail) {
        int sumOfRatios = 1; // ratio is always 1 for an end bit job
        Double quantity = getAssignedQuantity(jobDetail.getRatioDetails());
        getMvpView().updateLayLength(ocNumber, layLength, jobId, quantity, CommonUtils.getItemDescription(itemCode, itemDesc), sumOfRatios, getRatioListData(jobDetail.getRatioDetails()));
        getSavedBarcodes(jobId, jobDetail.getPartName());
    }

    /**
     * Fetches the saved barcodes for a given job
     * @param jobId Job Id to fetch the list for
     */

    private void getSavedBarcodes(Integer jobId, String partName) {
        getCompositeDisposable().add(getDataManager()
                .getScanBarcodes(jobId, partName)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<ScanBarcode>>() {
                    @Override
                    public void accept(List<ScanBarcode> scanBarcodeList) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().updateFromDatabase(scanBarcodeList);
                    }
                }));
    }

    /**
     * Returns a list of list of sizes and quantity
     *
     * @param ratioDetails List of RatioDetail from response
     * @return Size/Quantity Data
     */

    private List<List<String>> getRatioListData(List<FetchFollowerEndBitJobResponse.RatioDetail> ratioDetails){
        List<String> sizes = new ArrayList<String>();
        List<String> sizeQuantity = new ArrayList<String>();

        List<List<String>> listData = new ArrayList<>();

        for (FetchFollowerEndBitJobResponse.RatioDetail ratio:
                ratioDetails) {
            sizes.add(ratio.getSize());
            sizeQuantity.add(String.valueOf(ratio.getQuantity()));
        }

        listData.add(ListType.SIZE.getValue(), sizes);
        listData.add(ListType.QUANTITY.getValue(), sizeQuantity);

        return listData;
    }

    /**
     * SUm the size quantities to get assigned quantity
     * @param ratioDetails List of ratio details
     * @return Sum
     */

    private Double getAssignedQuantity(List<FetchFollowerEndBitJobResponse.RatioDetail> ratioDetails) {
        double sum = 0.0;
        for (FetchFollowerEndBitJobResponse.RatioDetail ratio:
                ratioDetails) {
            sum = sum + Double.valueOf(ratio.getQuantity());
        }

        return sum;
    }

    /**
     * Server call for validating the actual plies
     * Shows error if not valid
     * Shows check mark if valid
     * @param actualPlies Actual plies entered by user
     * @param barcode Barcode scanned
     * @param jobId Job Id
     * @param position View position
     */

    @Override
    public void validateActualPlies(Double actualPlies, String barcode, Integer jobId, String partName, int position) {
        ValidateActualPliesRequest request = new ValidateActualPliesRequest();
        request.setDeviceId(getMvpView().getDeviceId());
        request.setLocation(getDataManager().getLocation());
        request.setActualPlies(actualPlies);

        getCompositeDisposable().add(getDataManager()
                .doValidateActualPliesEndBitCall(jobId, barcode, partName, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().pliesValidated(position);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().pliesError(position);
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.validate_plies_error);
                        }
                    }
                }));
    }

    /**
     * Updates the given barcode in local database
     * @param item Barcode to be updated
     */

    @Override
    public void updateScanBarcode(ScanBarcode item) {
        getCompositeDisposable().add(getDataManager()
                .updateScanBarcode(item)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                    }
                }));
    }
}
