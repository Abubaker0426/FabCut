package com.indiandesigns.fabcut.ui.main;

import android.content.IntentSender;
import android.content.pm.PackageManager;

import com.auth0.android.Auth0;
import com.auth0.android.provider.AuthCallback;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.indiandesigns.fabcut.ui.base.MvpView;

import java.util.ArrayList;

public interface MainMvpView extends MvpView {

    void startAuth(Auth0 account, AuthCallback authCallback);

    String getDeviceIdString();

    void showRegisterButton();

    void showUnRegisterButton();

    void hideRegisterButton();

    void hideUnRegisterButton();

    void showLocationPickerDialog(ArrayList<String> list);

    void isLocationValid();

    void showUiBasedOnRole(String role);

    void startUpdateFlow(AppUpdateInfo appUpdateInfo) throws IntentSender.SendIntentException;

    void startLocationUpdates();

    String getCurrentVersion() throws PackageManager.NameNotFoundException;

    String getCurrentPackageName();

    void openPlayStore();

    void startAuthFlow();
}