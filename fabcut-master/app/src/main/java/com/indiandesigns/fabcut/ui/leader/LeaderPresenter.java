package com.indiandesigns.fabcut.ui.leader;

import android.content.DialogInterface;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.EndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFitTypesRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.FetchMarkersRequest;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.data.network.model.Marker;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LeaderPresenter<V extends LeaderMvpView> extends BasePresenter<V> implements LeaderMvpPresenter<V> {

    private static final String TAG = "LeaderPresenter";

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
    public LeaderPresenter(DataManager dataManager,
                           SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /***
     * Validates OC Number with server
     *
     * Populates Fit Types spinner if valid
     * Show error if not valid and clear spinner
     *
     * @param ocNo OC Number to be validated
     */

    @Override
    public void validateOcNumber(String ocNo) {
        FetchFitTypesRequest request = new FetchFitTypesRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchFitTypesCall(ocNo, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> response) throws Exception {
                        ArrayList<String> fitTypes = new ArrayList<>(response);
                        getMvpView().populateDropdownList(fitTypes);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.validate_oc_number_error);
                        }
                        getMvpView().clearDropDown();
                    }
                })
        );
    }

    /**
     * Fetch markers for entered OC Number and selected fit
     * @param ocNo Oc Number
     * @param selectedFit Fit type
     */

    @Override
    public void fetchMarkers(String ocNo, String selectedFit) {
        getMvpView().showLoading();
        getMvpView().hideKeyboard();
        FetchMarkersRequest request = new FetchMarkersRequest();
        request.setLocation(getDataManager().getLocation());
        request.setFitType(selectedFit);

        getCompositeDisposable().add(getDataManager()
                .doFetchMarkersCall(ocNo, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<Marker>>() {
                    @Override
                    public void accept(List<Marker> markerList) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        List<MarkerItem> markerItems = new ArrayList<>();
                        for (Marker marker:
                                markerList) {
                            MarkerItem item = new MarkerItem(marker.getMarkerUnique(), marker.getShrinkage(),
                                    marker.getItems(), ocNo);
                            markerItems.add(item);
                        }
                        getMvpView().updateMarkerList(markerItems);
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
                            handleApiError(anError, R.string.fetch_markers_error);
                        }
                    }
                })
        );
    }

    /***
     * Fetches the list of followers for device's location
     */

    @Override
    public void fetchFollowers() {
        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerDeviceIDsCall(getDataManager().getLocation())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<Follower>>() {
                    @Override
                    public void accept(List<Follower> followers) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().updateFollowers(followers);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.fetch_followers_error);
                        }
                    }
                }));
    }

    /**
     * Check whether the follower has a job or not
     * @param follower Follower for which we want to get the job
     */

    @Override
    public void getFollowerJob(Follower follower) {
        getMvpView().showLoading();

        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());


        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerJobCall(follower.getDeviceId(), request)
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
                            getFollowerEndBitJob(follower);
                        }else {
                            getMvpView().showAlertDialog(R.string.confirm_delete_job, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(i == DialogInterface.BUTTON_NEGATIVE){
                                        dialogInterface.dismiss();
                                    } else {
                                        dialogInterface.dismiss();
                                        if(response.getJobId() == null){
                                            getMvpView().onError(R.string.no_job_assigned);
                                        } else {
                                            deleteJob(response.getJobId());
                                        }
                                    }
                                }
                            });
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
     * Delete the job for the follower
     * @param jobId Job id of follower
     */

    private void deleteJob(int jobId){
        getCompositeDisposable().add(getDataManager()
                .doDeleteFollowerJobCall(jobId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
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
                            handleApiError(anError, R.string.delete_job_error);
                        }
                    }
                }));
    }

    /**
     * Fetch the list of assigned jobs
     *
     * Update the view
     */

    @Override
    public void fetchAssignedJobs() {
        getCompositeDisposable().add(getDataManager()
                .doFetchJobsCall(getDataManager().getLocation())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<JobListResponse>>() {
                    @Override
                    public void accept(List<JobListResponse> response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        List<OcLay> ocLays = CommonUtils.getUniqueOcLay(response);
                        getMvpView().hideLoading();
                        getMvpView().updateList(ocLays);
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
                            handleApiError(anError, R.string.fetch_job_assigned_error);
                        }
                    }
                })
        );
    }

    /**
     * Fetches the current assigned job to a follower
     * @param follower Selected Follower
     */

    @Override
    public void fetchFollowerJob(Follower follower) {
        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerJobCall(follower.getDeviceId(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchFollowerResponse>() {
                    @Override
                    public void accept(FetchFollowerResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        if(response.getJobId() == null){
                            fetchFollowerEndBitJob(follower);
                        } else {
                            getMvpView().hideLoading();
                            getMvpView().openFollowerDetailActivity(response);
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
     * Fetches the current assigned end bit job to a follower
     * @param follower Selected Follower
     */

    @Override
    public void fetchFollowerEndBitJob(Follower follower) {
        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerEndBitJobCall(follower.getDeviceId(), request)
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
                            getMvpView().showAlertDialog(R.string.no_job_assigned);
                        } else {
                            getMvpView().openFollowerDetailActivityForEndBit(response);
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
     * Checks if the follower has an end bit job assigned or not
     * @param follower The selected follower
     */

    @Override
    public void getFollowerEndBitJob(Follower follower){
        getMvpView().showLoading();

        FetchFollowerRequest request = new FetchFollowerRequest();
        request.setLocation(getDataManager().getLocation());


        getCompositeDisposable().add(getDataManager()
                .doFetchFollowerEndBitJobCall(follower.getDeviceId(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchFollowerEndBitJobResponse>() {
                    @Override
                    public void accept(FetchFollowerEndBitJobResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();

                        getMvpView().showAlertDialog(R.string.confirm_delete_job, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == DialogInterface.BUTTON_NEGATIVE){
                                    dialogInterface.dismiss();
                                } else {
                                    dialogInterface.dismiss();
                                    if(response.getEndBitJobId() == null){
                                        getMvpView().onError(R.string.no_job_assigned);
                                    } else {
                                        deleteEndBitJob(response.getEndBitJobId(), follower.getDeviceId());
                                    }
                                }
                            }
                        });

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
     * Fetch list of countries based on oc number
     * @param job The job details
     */
    @Override
    public void getCountriesList(OcLay job) {
        getMvpView().showLoading();
        String ocNo = job.getOcNo();
        getCompositeDisposable().add(getDataManager()
                .doFetchCountriesCall(ocNo)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                 .subscribe(new Consumer<List<String>>() {
                     @Override
                     public void accept(List<String> response) throws Exception {
                         if (!isViewAttached()) {
                             return;
                         }
                         getMvpView().hideLoading();
                         ArrayList<String> countriesList = new ArrayList<>(response);
                         getMvpView().showBundleSplitDialog(countriesList, job);
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
                             handleApiError(anError, R.string.fetch_country_error);
                         }

                     }
                 })
        );
    }

    /**
     * Delete the end bit job assigned to a follower
     * @param endBitJobId The end bit job id
     * @param deviceId The device id
     */
    private void deleteEndBitJob(Integer endBitJobId, String deviceId) {
        EndBitJobRequest request = new EndBitJobRequest();
        request.setDeviceId(deviceId);
        request.setLocation(getDataManager().getLocation());
        getCompositeDisposable().add(getDataManager()
                .doDeleteFollowerEndBitJobCall(endBitJobId, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().showMessage(R.string.end_bit_job_deleted);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.delete_job_error);
                        }
                    }
                }));
    }


}