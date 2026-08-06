package com.indiandesigns.fabcut.ui.leader.jobs.parts;

import com.androidnetworking.error.ANError;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.LocationRequest;
import com.indiandesigns.fabcut.data.network.model.PutPartsDetailsRequest;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class BundlePartsPresenter<V extends BundlePartsMvpView> extends BasePresenter<V> implements BundlePartsMvpPresenter<V> {
    /**
     * Parameterized Constructor
     *
     * Instantiates data operator classes
     *
     * @param dataManager         Injected with Dagger
     * @param schedulerProvider   Injected with Dagger
     * @param compositeDisposable Injected with Dagger
     */
    @Inject
    public BundlePartsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * This function is responsible call API to validate oc number and fetch part details
     * @param ocNo Oc number to get parts details
     */
    @Override
    public void validateOcNumberAndGetParts(String ocNo) {
        getMvpView().showLoading(R.string.fetch_parts);

        LocationRequest request = new LocationRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchPartsForOCCall(ocNo, request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchPartsResponse>() {
                    @Override
                    public void accept(FetchPartsResponse response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().hideLoading();
                        handleBundlePartsResponse(response);
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
                        getMvpView().showErrorIconForOCNumber();
                    }
                })
        );

    }

    /**
     * This function is responsible call API to update selected parts
     * @param ocNo Oc number to get parts details
     * @param PartsDetailList List of {@link FetchPartsDetails}
     */

    @Override
    public void callPutSelectedBundleParts(String ocNo, List<FetchPartsDetails> PartsDetailList) {
        PutPartsDetailsRequest request = new PutPartsDetailsRequest(PartsDetailList);
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
        .doPutSelectedBundleParts(ocNo, request)
        .subscribeOn(getSchedulerProvider().io())
        .observeOn(getSchedulerProvider().ui())
        .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().closeBundlePartsActivity();
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
                            handleApiError(anError, R.string.put_bundle_parts_error);
                        }

                    }
                })
        );

    }

    /**
     * This function is responsible to validate is part selected in activity for associated oc number
     * @param partsDetailList List of {@link FetchPartsDetails}
     * @return boolean
     */
    @Override
    public boolean validateSelectedPartsList(List<FetchPartsDetails> partsDetailList) {
        for (FetchPartsDetails fetchPartsDetails : partsDetailList) {
            if(fetchPartsDetails.getIsSelected() == 1){
                return true;
            }
        }
        return false;
    }


    /**
     * Handles the bundle parts response list
     * @param response {@link FetchPartsResponse}
     */
    public void handleBundlePartsResponse(FetchPartsResponse response){
        List<FetchPartsDetails> bundlePartsList = response.getParts();
        getMvpView().updatePartsResponse(bundlePartsList);
    }
}
