package com.indiandesigns.fabcut.ui.base;

import android.text.TextUtils;
import android.util.Log;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.error.ANError;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.ApiError;
import com.indiandesigns.fabcut.utils.AppConstants;
import com.indiandesigns.fabcut.utils.AppLogger;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import io.reactivex.disposables.CompositeDisposable;

/**
 *Extended by all presenters
 * Provides all data operators with Dependency injection
 */

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private static final String TAG = "BasePresenter";

    private final DataManager mDataManager;
    private final SchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mCompositeDisposable;

    private V mMvpView;

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
    public BasePresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = compositeDisposable;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void onDetach() {
        mCompositeDisposable.dispose();
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    /**
     * Fetches the DataManager
     *
     * @return DataManager instance
     */

    public DataManager getDataManager() {
        return mDataManager;
    }

    /**
     * Fetches the SchedulerProvider
     *
     * @return SchedulerProvider instance
     */

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    /**
     * Fetches the CompositeDisposable
     *
     * @return CompositeDisposable instance
     */

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    /**
     * Handles the different error responses from API calls
     * and shows appropriate message on SnackBar
     *
     * @param error Error received from API calls
     */

    @Override
    public void handleApiError(ANError error, int defaultError) {
        if (error == null || error.getErrorBody() == null) {
            getMvpView().onError(defaultError);
            return;
        }

        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
                && error.getErrorDetail().equals(ANConstants.CONNECTION_ERROR)) {
            getMvpView().onError(R.string.connection_error);
            return;
        }

        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
                && error.getErrorDetail().equals(ANConstants.REQUEST_CANCELLED_ERROR)) {
            getMvpView().onError(R.string.api_retry_error);
            return;
        }

        final GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();

        try {
            if(error.getErrorCode() >= 400){
                ApiError apiError = gson.fromJson(error.getErrorBody(), ApiError.class);
                if (apiError == null || apiError.getMessage() == null) {
                    getMvpView().onError(defaultError);
                    return;
                }

                getMvpView().onError(apiError.getMessage());
            } else {
                getMvpView().onError(defaultError);
            }
        } catch (JsonSyntaxException | NullPointerException e) {
            AppLogger.e(TAG, "handleApiError", e);
            getMvpView().onError(R.string.api_default_error);
        }
    }

    /**
     * Acquires new access token from Auth0 and updates
     * the shared preferences
     */

    private void refreshToken() {
        Auth0 auth0 = new Auth0(getMvpView().getString(R.string.com_auth0_client_id), getMvpView().getString(R.string.com_auth0_domain));
        auth0.setOIDCConformant(true);
        AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
        client.renewAuth(getDataManager().getRefreshToken())
                .addParameter("scope", "openid email")
                .start(new BaseCallback<Credentials, AuthenticationException>() {
                    @Override
                    public void onSuccess(Credentials credentials) {
                        String accessToken = credentials.getAccessToken();
                        getDataManager().setAccessToken("Bearer " + accessToken);
                        getDataManager().setRefreshToken(credentials.getRefreshToken());
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        getMvpView().onError(error.getMessage());
                    }
                });
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
