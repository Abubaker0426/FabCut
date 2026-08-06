package com.indiandesigns.fabcut.ui.main;

import android.location.Location;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void isLocationValid();

    void isDeviceRegistered();

    void register(String role, int tableNumber);

    void unregister();

    void startAuthFlow(Auth0 account);

    void setCredentialsManager(CredentialsManager manager);

    void checkForUpdate(AppUpdateManager appUpdateManager);

    void doValidateLocationCall(Location location);
}