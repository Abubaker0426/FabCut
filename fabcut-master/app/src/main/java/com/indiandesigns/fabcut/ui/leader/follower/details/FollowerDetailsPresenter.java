package com.indiandesigns.fabcut.ui.leader.follower.details;

import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

public class FollowerDetailsPresenter<V extends FollowerDetailsMvpView> extends BasePresenter<V> implements FollowerDetailsMvpPresenter<V> {

    private static final String TAG = "FollowerDetailsPresenter";

    @Inject
    public FollowerDetailsPresenter(DataManager dataManager,
                                    SchedulerProvider schedulerProvider,
                                    CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Parse job details response and update view
     *
     * @param response Job details response for follower
     */

    @Override
    public void parseDetails(FetchFollowerResponse response) {
        for (FetchFollowerResponse.JobDetail jobDetail: response.getJobDetails()){
            getMvpView().updateView(CommonUtils.getItemDescription(jobDetail.getItemCode(), jobDetail.getItemDesc()), getRatioListData(jobDetail.getRatioDetails()));
        }
    }

    /**
     * Returns a list of list of sizes and quantity
     *
     * @param ratioDetails List of RatioDetail from response
     * @return Size/Quantity Data
     */

    private List<List<String>> getRatioListData(List<FetchFollowerResponse.RatioDetail> ratioDetails){
        List<String> sizes = new ArrayList<String>();
        List<String> sizeQuantity = new ArrayList<String>();

        List<List<String>> listData = new ArrayList<>();

        for (FetchFollowerResponse.RatioDetail ratio:
                ratioDetails) {
            sizes.add(ratio.getSize());
            sizeQuantity.add(String.valueOf(ratio.getQuantity()));
        }

        listData.add(ListType.SIZE.getValue(), sizes);
        listData.add(ListType.QUANTITY.getValue(), sizeQuantity);

        return listData;
    }

    /**
     * Parse job details response and update view
     *
     * @param response End bit job details response for follower
     */
    @Override
    public void parseEndBitDetails(FetchFollowerEndBitJobResponse response) {
        for (FetchFollowerEndBitJobResponse.JobDetail jobDetail: response.getJobDetails()){
            getMvpView().updateView(CommonUtils.getItemDescription(response.getItemCode(), jobDetail.getPartName()), getRatioListDataForEndBit(jobDetail.getRatioDetails()));
        }
    }

    /**
     * Returns a list of list of sizes and quantity for an end bit job
     *
     * @param ratioDetails List of RatioDetail from response
     * @return Size/Quantity Data
     */

    private List<List<String>> getRatioListDataForEndBit(List<FetchFollowerEndBitJobResponse.RatioDetail> ratioDetails){
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
}