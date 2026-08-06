package com.indiandesigns.fabcut.ui.follower;

import android.content.DialogInterface;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.enums.JobType;
import com.indiandesigns.fabcut.data.network.model.EndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.ItemQuantityData;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import javax.inject.Inject;

public class FollowerPresenter<V extends FollowerMvpView> extends BasePresenter<V> implements FollowerMvpPresenter<V> {

    private static final String TAG = "FollowerPresenter";

    /**
     * Parameterized Constructor
     *
     * Instantiates data operator classes
     *
     * @param dataManager Injected with Dagger
     * @param schedulerProvider Injected with Dagger
     * @param compositeDisposable Injected with Dagger
     */

    @Inject
    public FollowerPresenter(DataManager dataManager,    /**
     * Fetches the current cut and assigned quantities for all item codes
     * @return Map of quantities data with item code
     */
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Validate all collective plies for job details
     * Complete the job if user confirms
     *
     * @param jobId Job Id
     */

    @Override
    public void validatedAllPlies(Integer jobId, JobType jobType) {
        getCompositeDisposable().add(getDataManager()
                .getScanBarcodes(jobId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<ScanBarcode>>() {
                    @Override
                    public void accept(List<ScanBarcode> scanBarcodeList) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        boolean validated = true;
                        for (ScanBarcode barcode:
                             scanBarcodeList) {
                            if(barcode.getValidated() == null || !barcode.getValidated()){
                                validated = false;
                            }
                        }
                        if(validated){
                            HashMap<String, ItemQuantityData> quantityDataHashMap = getDataManager().getItemQuantities();
                            double assignedSum = 0.0;
                            double cutSum = 0.0;
                            for (ItemQuantityData data : quantityDataHashMap.values()) {
                                assignedSum += data.getAssigned();
                                cutSum += data.getCut();
                            }
                            if(cutSum <= 0.0){
                                getMvpView().onError(R.string.cut_quantity_error);
                            } else {
                                completeJob(jobId, assignedSum, cutSum, jobType);
                            }
                        } else {
                            getMvpView().onError(R.string.actual_plies_error);
                        }
                    }
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }
                }));
    }

    /**
     * Fetches the follower's job details as soon as view is prepared
     */

    @Override
    public void onViewPrepared() {
        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());


        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerJobCall(getMvpView().getDeviceIdString(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchFollowerResponse>() {
                    @Override
                    public void accept(FetchFollowerResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        if(response.getJobId() == null){
                            fetchEndBitJob();
                        } else {
                            getMvpView().updateViewPager(response.getJobId(), response.getLayLength(), response.getOcNumber(), response.getJobDetails());
                            getMvpView().showMainLayout();
                        }
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
                            handleApiError(anError, R.string.fetch_job_details_error);
                        }
                    }
                }));
    }

    /**
     * Checks and fetches the follower's end bit job details
     */
    @Override
    public void fetchEndBitJob() {
        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerEndBitJobCall(getMvpView().getDeviceIdString(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchFollowerEndBitJobResponse>() {
                    @Override
                    public void accept(FetchFollowerEndBitJobResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        if(response.getEndBitJobId() == null){
                            getMvpView().hideMainLayout();
                        } else {
                            getMvpView().updateViewEndBitPager(response.getEndBitJobId(), response.getOcNumber(), response.getItemCode(), response.getItemDesc(),response.getJobDetails());
                            getMvpView().showMainLayout();
                        }
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
                            handleApiError(anError, R.string.fetch_job_details_error);
                        }
                    }
                }));
    }

    /**
     * Shows confirmation message and calls the complete job api
     * If user confirms
     * @param jobId Job id
     * @param assigned Assigned quantity
     * @param cut Cut quantity
     */

    @Override
    public void completeJob(Integer jobId, Double assigned, Double cut, JobType jobType) {
        getMvpView().showAlertDialog(R.string.confirm_submit_job, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogInterface.BUTTON_NEGATIVE){
                    dialogInterface.dismiss();
                } else {
                    dialogInterface.dismiss();
                    if(cut < assigned){
                        showConfirmation(jobId, jobType);
                    } else {
                        if(jobType == JobType.END_BIT) {
                            completeEndBitJobApi(jobId);
                            return;
                        }
                        completeJobApi(jobId);
                    }
                }
            }
        });
    }

    /**
     * Shows confirmation popup with Red text
     * @param jobId Job Id
     */

    private void showConfirmation(Integer jobId, JobType jobType) {
        getMvpView().showAlertDialog(getMvpView().getString(R.string.confirm_assigned_quantity_more), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogInterface.BUTTON_NEGATIVE){
                    dialogInterface.dismiss();
                } else {
                    dialogInterface.dismiss();
                    if(jobType == JobType.END_BIT) {
                        completeEndBitJobApi(jobId);
                        return;
                    }
                    completeJobApi(jobId);
                }
            }
        });
    }

    /**
     * Calls the complete job api and submits the job
     * @param jobId Job Id
     */

    private void completeJobApi(Integer jobId){
        getCompositeDisposable().add(getDataManager()
                .doCompleteFollowerJobCall(jobId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        onViewPrepared();
                        getMvpView().showConfirmationDialog(R.string.job_completed);
                        deleteBarcodes(jobId);
                        getDataManager().clearItemQuantityData();
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
                            handleApiError(anError, R.string.complete_job_error);
                        }
                    }
                }));
    }

    /**
     * Calls the complete end bit job api and submits the job
     * @param jobId End bit job Id
     */

    private void completeEndBitJobApi(Integer jobId){
        EndBitJobRequest request = new EndBitJobRequest();
        request.setDeviceId(getMvpView().getDeviceIdString());
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doCompleteFollowerEndBitJobCall(jobId, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        onViewPrepared();
                        getMvpView().showConfirmationDialog(R.string.end_bit_job_completed);
                        deleteBarcodes(jobId);
                        getDataManager().clearItemQuantityData();
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
                            handleApiError(anError, R.string.complete_job_error);
                        }
                    }
                }));
    }


    /**
     * Deletes the barcodes in local database for a given job id
     * @param jobId Job Id
     */

    private void deleteBarcodes(Integer jobId) {
        getCompositeDisposable().add(getDataManager()
                .deleteScanBarcodes(jobId)
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