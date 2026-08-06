package com.indiandesigns.fabcut.ui.main.location_picker_dialog;

import com.indiandesigns.fabcut.ui.base.MvpPresenter;

/**
 * Implemented by {@link LocationPickerDialogPresenter}
 */

public interface LocationPickerDialogMvpPresenter<V extends LocationPickerDialogMvpView> extends MvpPresenter<V> {

    void updateLocation(String location);

}