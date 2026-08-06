package com.indiandesigns.fabcut.ui.main.location_picker_dialog;

import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Performs all the backend processing for {@link LocationPickerDialog}
 */

public class LocationPickerDialogPresenter<V extends LocationPickerDialogMvpView> extends BasePresenter<V> implements LocationPickerDialogMvpPresenter<V> {

    private static final String TAG = "LocationPickerDialogPresenter";

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
    public LocationPickerDialogPresenter(DataManager dataManager,
                                         SchedulerProvider schedulerProvider,
                                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Updates the location in database
     *
     * @param location Location selected by user
     */

    @Override
    public void updateLocation(String location) {
        getDataManager().setLocation(location);
        getMvpView().dismissDialog();
    }
}