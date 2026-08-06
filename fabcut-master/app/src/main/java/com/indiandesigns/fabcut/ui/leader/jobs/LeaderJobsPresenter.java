package com.indiandesigns.fabcut.ui.leader.jobs;

import android.content.DialogInterface;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.EndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.JobDetails;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.data.network.model.LayLength;
import com.indiandesigns.fabcut.data.network.model.Ratio;
import com.indiandesigns.fabcut.data.network.model.SizeQuantity;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LeaderJobsPresenter<V extends LeaderJobsMvpView> extends BasePresenter<V> implements LeaderJobsMvpPresenter<V> {

    private static final String TAG = "LeaderJobsPresenter";

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
    public LeaderJobsPresenter(DataManager dataManager,
                               SchedulerProvider schedulerProvider,
                               CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
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

    /***
     * Fetch job details for a given Marker
     *
     * @param marker Marker selected
     */

    @Override
    public void fetchJobDetails(String marker) {
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .doFetchJobDetailsCall(marker)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<JobDetails>() {
                    @Override
                    public void accept(JobDetails response) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        processRatioData(response.getRatios(), response.getSizeQuantities(), response.getLayLengths());
                        fetchFollowers();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
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

    private void processRatioData(List<Ratio> ratios, List<SizeQuantity> sizeQuantities, List<LayLength> layLengths) {
        HashMap<Integer, List<List<String>>> newRatioData = new HashMap<Integer, List<List<String>>>();

        // map holding a list of ratio relating to only that ratioNumber
        HashMap<Integer, List<Ratio>> ratioListHashMap = new HashMap<Integer, List<Ratio>>();

        // put a list of ratios with same ratioNumber with the key(ratioNumber)
        for (Ratio ratio:
             ratios) {
            Integer ratioId = ratio.getRatioNumber();
            if (!ratioListHashMap.containsKey(ratioId)) {
                List<Ratio> list = new ArrayList<Ratio>();
                list.add(ratio);
                ratioListHashMap.put(ratioId, list);
            } else {
                ratioListHashMap.get(ratioId).add(ratio);
            }
        }

        // for each entry on the ratioListHashMap, filter out the ratios in three different lists and add it to the newRatioData
        for (Map.Entry<Integer, List<Ratio>> entry : ratioListHashMap.entrySet()) {
            Integer key = entry.getKey();
            List<Ratio> value = entry.getValue();
            addNewRatio(newRatioData, value, key, sizeQuantities, layLengths);
        }

        getMvpView().updateJobsList(newRatioData);
    }

    private void addNewRatio(HashMap<Integer, List<List<String>>> newRatioData, List<Ratio> ratioList, Integer key, List<SizeQuantity> sizeQuantities, List<LayLength> layLengths) {
        // create three lists for each row of a job
        List<String> sizes = new ArrayList<String>();
        List<String> quantities = new ArrayList<String>();
        List<String> ratios = new ArrayList<String>();
        List<String> sizeQuantity = new ArrayList<String>();
        List<String> sizeAvailableQuantity = new ArrayList<String>();
        List<String> sizeCompletedQuantity = new ArrayList<String>();

        List<List<String>> listDataForRatio = new ArrayList<>();

        // traverse the lists for a ratioNumber(key) and add them to the listDataForRatio
        for (Ratio ratio:
                ratioList) {
            sizes.add(String.valueOf(ratio.getSize()));

            Integer quantity = findSizeQuantity(sizeQuantities, ratio.getSize());
            Integer completedQuantity = findSizeCompletedQuantity(sizeQuantities, ratio.getSize());
            Integer available = quantity - completedQuantity;

            sizeQuantity.add(String.valueOf(quantity));
            sizeCompletedQuantity.add(String.valueOf(completedQuantity));
            sizeAvailableQuantity.add(String.valueOf(available));
            quantities.add(String.valueOf(ratio.getRatioQty()));
            ratios.add(String.valueOf(ratio.getRatio()));
        }

        sizes.add(getMvpView().getString(R.string.lay_length));
        sizeCompletedQuantity.add("");
        sizeAvailableQuantity.add("");
        sizeQuantity.add("");
        quantities.add(findLayLength(layLengths, key));
        ratios.add("");

        sizes.add(getMvpView().getString(R.string.total));
        sizeCompletedQuantity.add("");
        sizeAvailableQuantity.add("");
        sizeQuantity.add("");

        int totalQuantity = sum(quantities);

        ratios.add("");
        quantities.add(String.valueOf(totalQuantity));

        listDataForRatio.add(ListType.SIZE.getValue(), sizes);
        listDataForRatio.add(ListType.QUANTITY.getValue(), quantities);
        listDataForRatio.add(ListType.RATIO.getValue(), ratios);
        listDataForRatio.add(ListType.SIZE_QUANTITY.getValue(), sizeQuantity);
        listDataForRatio.add(ListType.SIZE_COMPLETED_QUANTITY.getValue(), sizeCompletedQuantity);
        listDataForRatio.add(ListType.SIZE_AVAILABLE_QUANTITY.getValue(), sizeAvailableQuantity);

        // add the list containing the three lists to newRatioData
        newRatioData.put(key, listDataForRatio);
    }

    /***
     * Sums the property in a given list of string
     *
     * @param list List of data
     * @return Sum of data
     */

    public int sum(List<String> list) {
        int sum = 0;
        for (int i = 0; i < list.size() - 1; i++){
            String value = list.get(i);
            if(!value.equals("")){
                sum = sum + Integer.valueOf(value);
            }
        }
        return sum;
    }

    /**
     * FInd lay length for a ratio
     * @param sizeQuantities List of size quantities
     * @param ratio Ratio
     * @return Lay length
     */

    private String findLayLength(List<LayLength> sizeQuantities, Integer ratio){
        for (LayLength layLength:
                sizeQuantities) {
            if(layLength.getRatioNumber() == ratio){
                return String.valueOf(layLength.getLayLength());
            }
        }
        return String.valueOf(0);
    }

    /**
     * Find size quantity for a given size
     * @param sizeQuantities List of size quantities
     * @param size Size
     * @return Size quantity
     */

    private Integer findSizeQuantity(List<SizeQuantity> sizeQuantities, String size){
        for (SizeQuantity quantity:
             sizeQuantities) {
            if(quantity.getSize().equalsIgnoreCase(size)){
                return quantity.getQty();
            }
        }
        return 0;
    }

    /**
     * Find size completed quantity for a given size
     * @param sizeQuantities List of size quantities
     * @param size Size
     * @return Size completed quantity
     */

    private Integer findSizeCompletedQuantity(List<SizeQuantity> sizeQuantities, String size){
        for (SizeQuantity quantity:
                sizeQuantities) {
            if(quantity.getSize().equalsIgnoreCase(size)){
                return quantity.getCompletedQty();
            }
        }
        return 0;
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
     * Calls the assign job api and assigns the job to follower
     * @param request Request to be sent to server
     * @param follower Follower to assign the job
     */

    @Override
    public void assignJob(AssignJobRequest request, Follower follower) {
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .doAssignJobToFollowerCall(follower.getDeviceId(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer response) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().showConfirmationDialog(R.string.job_assigned);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.job_assign_error);
                        }
                    }
                }));
    }

    /**
     * Calls the assign end bit job api and assigns the end bit job to follower
     * @param request Request to be sent to the server
     * @param follower Follower to be assigned
     */

    @Override
    public void assignEndBitJob(AssignEndBitJobRequest request, Follower follower){
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .doAssignEndBitJobToFollowerCall(follower.getDeviceId(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().showConfirmationDialog(R.string.job_assigned);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.job_assign_error);
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
                        }else{
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
                        getMvpView().updateAssignedJobsList(ocLays);
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