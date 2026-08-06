package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchLayNumbersRequest;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.data.network.model.LocationRequest;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static com.indiandesigns.fabcut.utils.AppConstants.LAY_LENGTH_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_GROUP_TAG;

public class EditEndBitJobDialogPresenter<V extends EditEndBitJobDialogMvpView> extends BasePresenter<V> implements EditEndBitJobDialogMvpPresenter<V> {

    private static final String TAG = "EditJobDialogPresenter";

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
    public EditEndBitJobDialogPresenter(DataManager dataManager,
                                  SchedulerProvider schedulerProvider,
                                  CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Prepares data model to be assigned to a follower
     * @param sizeContainer Size values layout
     * @param quantitiesContainer Quantity values layout
     * @param itemList Item list
     * @param itemList
     */

    @Override
    public void prepareJobData(LinearLayout sizeContainer, LinearLayout quantitiesContainer, List<Item> itemList, String layNumber, List<String> selectedParts) {

        AssignEndBitJobRequest request = new AssignEndBitJobRequest();
        request.setLocation(getDataManager().getLocation());
        request.setItemCode(itemList.get(0).getItemCode());
        request.setItemDesc(itemList.get(0).getItemDesc());
        request.setLayNumber(Integer.valueOf(layNumber));
        List<AssignEndBitJobRequest.JobDetail> jobDetails = new ArrayList<AssignEndBitJobRequest.JobDetail>();

        // Traverses the array of TextViews and EditTextAssignJobRequests and prepares the data model

        final int childCount = sizeContainer.getChildCount();

        int partIndex = 0;
        if(selectedParts.size() != quantitiesContainer.getChildCount()/2){
            getMvpView().showMessage(R.string.job_assign_error);
            return;
        }

        for(int itemIndex = 0; itemIndex < quantitiesContainer.getChildCount(); itemIndex++){
            if(quantitiesContainer.getChildAt(itemIndex).getTag() != null){
                LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(itemIndex);
                AssignEndBitJobRequest.JobDetail jobDetail = request.new JobDetail();
                List<AssignEndBitJobRequest.RatioDetail> ratioDetailList = new ArrayList<>();

                String layLength = ((TextView) quantityContainer.findViewWithTag(LAY_LENGTH_TAG)).getText().toString();

                if(TextUtils.isEmpty(layLength)){
                    getMvpView().showMessage(R.string.lay_length_error);
                    return;
                }
                jobDetail.setLayLength(Double.valueOf(layLength));

                for (int index = 0; index < childCount - 2; index++) {
                    AssignEndBitJobRequest.RatioDetail ratioDetail = request.new RatioDetail();

                    String size = ((TextView) sizeContainer.getChildAt(index)).getText().toString();
                    String quantity = ((EditText) quantityContainer.getChildAt(index)).getText().toString();

                    ratioDetail.setQuantity(Integer.valueOf(quantity));
                    ratioDetail.setSize(size);
                    ratioDetailList.add(ratioDetail);
                }
                jobDetail.setPartName(selectedParts.get(partIndex++));
                jobDetail.setRatioDetails(ratioDetailList);
                jobDetails.add(jobDetail);
            }
        }
        
        request.setJobDetails(jobDetails);
        getMvpView().assignJob(request);
    }

    /**
     * Fetch list of lay numbers for end bits job dialog box
     * @param markerItem Marker item
     */
    @Override
    public void fetchLayNumbers(MarkerItem markerItem){
        FetchLayNumbersRequest request = new FetchLayNumbersRequest();
        if(markerItem.getItems().size()== 0){
            getMvpView().showMessage("No marker item found");
            return;
        }
        request.setItemCode(markerItem.getItems().get(0).getItemCode());
        request.setLocation(getDataManager().getLocation());
        getCompositeDisposable().add(getDataManager()
                .doFetchLayNumbersCall(markerItem.getMarkerUnique(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<Number>>() {
                    @Override
                    public void accept(List<Number> response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().updateLayNumbersList(response);
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
                            handleApiError(anError, R.string.fetch_lay_error);
                        }
                    }
                })
        );
    }

    /**
     * Fetch list of part names based on OC number
     * @param ocNum The oc number
     */
    @Override
    public void fetchPartNames(String ocNum){
        LocationRequest request = new LocationRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchPartsForOCCall(ocNum, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchPartsResponse>() {
                    @Override
                    public void accept(FetchPartsResponse response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().updatePartNamesList(response.getParts());
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
                            handleApiError(anError, R.string.fetch_parts_error);
                        }
                    }
                })
        );
    }


}