package com.indiandesigns.fabcut.ui.main;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;

import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.result.Credentials;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.enums.Role;
import com.indiandesigns.fabcut.data.network.model.IsDevRegRequest;
import com.indiandesigns.fabcut.data.network.model.IsDevRegResponse;
import com.indiandesigns.fabcut.data.network.model.LocRequest;
import com.indiandesigns.fabcut.data.network.model.RegDevRequest;
import com.indiandesigns.fabcut.data.network.model.UnregisterDevRequest;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialog;
import com.indiandesigns.fabcut.utils.AppLogger;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import javax.inject.Inject;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    private static final String TAG = "MainPresenter";

    private CredentialsManager _manager;

    private String currentVersion = null;
    private String currentPackageName = null;

    private boolean isAppUpdated = true;

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param v1 a string of alpha numerals separated by decimal points.
     * @param v2 a string of alpha numerals separated by decimal points.
     * @return The result is 1 if v1 is greater than v2.
     *         The result is 2 if v2 is greater than v1.
     *         The result is -1 if the version format is unrecognized.
     *         The result is zero if the strings are equal.
     */

    private int VersionCompare(String v1,String v2)
    {
        int v1Len= StringUtils.countMatches(v1,".");
        int v2Len=StringUtils.countMatches(v2,".");

        if(v1Len!=v2Len)
        {
            int count=Math.abs(v1Len-v2Len);
            if(v1Len>v2Len)
                for(int i=1;i<=count;i++)
                    v2+=".0";
            else
                for(int i=1;i<=count;i++)
                    v1+=".0";
        }

        if(v1.equals(v2))
            return 0;

        String[] v1Str=StringUtils.split(v1, ".");
        String[] v2Str=StringUtils.split(v2, ".");
        for(int i=0;i<v1Str.length;i++)
        {
            String str1="",str2="";
            for (char c : v1Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str1+=String.valueOf("0"+u);
                    else
                        str1+=String.valueOf(u);
                }
                else
                    str1+=String.valueOf(c);
            }
            for (char c : v2Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str2+=String.valueOf("0"+u);
                    else
                        str2+=String.valueOf(u);
                }
                else
                    str2+=String.valueOf(c);
            }
            v1Str[i]="1"+str1;
            v2Str[i]="1"+str2;

            int num1=Integer.parseInt(v1Str[i]);
            int num2=Integer.parseInt(v2Str[i]);

            if(num1!=num2)
            {
                if(num1>num2)
                    return 1;
                else
                    return 2;
            }
        }
        return -1;
    }

    @Override
    public void setCredentialsManager(CredentialsManager manager) {
        _manager = manager;
    }

    /**
     * Called by the MainActivity class to start the authentication flow
     * Checks if:
     * - there is already a set of credentials stored by CredentialsManager
     * - if not stored, call back to {@link MainActivity} to start an authentication flow with Auth0
     * @param account Auth0 account object provided by Auth0 API
     */
    @Override
    public void startAuthFlow(Auth0 account) {
        _manager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(Credentials payload) {
                getDataManager().setAccessToken("Bearer " + payload.getAccessToken());
                getDataManager().setRefreshToken(payload.getRefreshToken());
                getMvpView().isLocationValid();
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                getMvpView().showLoading();
                getMvpView().startAuth(account, authCallback);
            }
        });
    }

    /***
     * Auth callback
     */

    private AuthCallback authCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull Dialog dialog) {
            getMvpView().hideLoading();
            getMvpView().onError(R.string.some_error);
        }

        @Override
        public void onFailure(AuthenticationException exception) {
            getMvpView().hideLoading();
            getMvpView().onError(R.string.auth_error);
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            getMvpView().hideLoading();
            _manager.saveCredentials(credentials);
            getDataManager().setAccessToken("Bearer " + credentials.getAccessToken());
            getDataManager().setRefreshToken(credentials.getRefreshToken());
            getMvpView().isLocationValid();
        }
    };

    /**
     * Get update information
     *
     * Start update flow if update is available or already running
     */

    @Override
    public void checkForUpdate(AppUpdateManager appUpdateManager) {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdate(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdate(appUpdateInfo);
            } else {
                AppLogger.e(appUpdateInfo.updateAvailability() + " " + appUpdateInfo.toString());
            }

        });

        appUpdateInfoTask.addOnFailureListener(failureInfo -> {
           AppLogger.e(failureInfo.getLocalizedMessage());
        });
    }

    /**
     * Start update flow with update information
     *
     * @param appUpdateInfo Update information available via App Update Manager
     */

    private void startUpdate(AppUpdateInfo appUpdateInfo){
        try{
            getMvpView().startUpdateFlow(appUpdateInfo);
        } catch(IntentSender.SendIntentException e) {
            AppLogger.e(e.getLocalizedMessage());
        }
    }

    /**
     * Gets the current location using FusedLocationProviderClient
     */

    @Override
    public void isLocationValid() {
        getMvpView().showLoading();
        getMvpView().startLocationUpdates();
    }

    /**
     * Processes the Validate Location API call
     * <p>
     * If response successful, call updateLocation()
     * <p>
     * If response failed
     * Show message from API response via handleApiError()
     *
     * @param location Last location of device
     */

    @Override
    public void doValidateLocationCall(Location location) {

        LocRequest request = new LocRequest();
        request.setLatitude(location.getLatitude());
        request.setLongitude(location.getLongitude());

        getCompositeDisposable().add(getDataManager()
                .doValidateLocationCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        ArrayList<String> locations = new ArrayList<>(response);
                        if (locations.size() == 1) {
                            getDataManager().setLocation(locations.get(0));
                            isDeviceRegistered();
                        } else {
                            getMvpView().showLocationPickerDialog(locations);
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
                            handleApiError(anError, R.string.validate_location_error);
                        }
                    }
                }));
    }

    /**
     * Registers the current device based on the Device ID and role selected by user
     * <p>
     * if successfully registered then
     * hides the register button
     * and shows the unregister button
     */

    @Override
    public void register(String role, int tableNumber) {
        if(role.toLowerCase().equals(Role.FOLLOWER.getValue())){
            if(tableNumber <= 0 || tableNumber > 10){
                getMvpView().hideKeyboard();
                getMvpView().onError(R.string.table_number_error);
                return;
            }
        }

        getMvpView().showLoading();

        String location = getDataManager().getLocation();
        RegDevRequest request = new RegDevRequest();
        request.setLocation(location);
        request.setRole(role);
        request.setTableNumber(tableNumber);

        getCompositeDisposable().add(getDataManager()
                .doRegisterDeviceCall(getMvpView().getDeviceIdString(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getDataManager().setRole(role);
                        getMvpView().hideLoading();
                        getMvpView().showUnRegisterButton();
                        getMvpView().hideRegisterButton();
                        getMvpView().showUiBasedOnRole(role);

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
                            handleApiError(anError, R.string.register_error);
                        }
                    }
                }));
    }

    /**
     * Unregisters the current device based on the Device ID
     * <p>
     * if successfully unregistered then
     * hides the unregister button
     * and shows the register button
     */

    @Override
    public void unregister() {
        getMvpView().showLoading();

        String location = getDataManager().getLocation();
        UnregisterDevRequest request = new UnregisterDevRequest();
        request.setLocation(location);
        request.setRole(getDataManager().getRole());

        getCompositeDisposable().add(getDataManager()
                .doUnregisterDeviceCall(getMvpView().getDeviceIdString(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getDataManager().deleteRole();
                        getMvpView().hideLoading();
                        getMvpView().showRegisterButton();
                        getMvpView().hideUnRegisterButton();

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
                            handleApiError(anError, R.string.unregister_error);
                        }
                    }
                }));
    }

    /**
     * Function that is called from {@link LocationPickerDialog} when the user selects a location
     * or from doValidateLocation of {@link MainPresenter} if there is only one possible location
     */
    @Override
    public void isDeviceRegistered() {
        doIsDeviceRegisteredCall();
    }

    /**
     * Checks if device is registered or not
     * <p>
     * Hides the register button if registered
     * Hides the unregister button if not registered
     */

    private void doIsDeviceRegisteredCall() {

        String location = getDataManager().getLocation();
        IsDevRegRequest request = new IsDevRegRequest();
        request.setLocation(location);

        getCompositeDisposable().add(getDataManager()
                .doIsDeviceRegisteredCall(getMvpView().getDeviceIdString(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<IsDevRegResponse>() {
                    @Override
                    public void accept(IsDevRegResponse response) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        boolean devRegStatus = response.getDevRegStatus();
                        if (devRegStatus) {
                            getMvpView().showUnRegisterButton();
                            getMvpView().hideRegisterButton();
                            getDataManager().setRole(response.getRole());
                            getMvpView().showUiBasedOnRole(response.getRole());
                        } else {
                            getMvpView().showRegisterButton();
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
                            handleApiError(anError, R.string.validate_device_error);
                        }
                    }
                }));
    }
}