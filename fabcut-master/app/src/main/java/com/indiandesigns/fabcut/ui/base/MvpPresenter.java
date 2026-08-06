package com.indiandesigns.fabcut.ui.base;

import com.androidnetworking.error.ANError;

/**
 * Interface extended by all presenters
 *
 * Contains common functionality for presenters
 */

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void handleApiError(ANError error, int defaultError);
}
