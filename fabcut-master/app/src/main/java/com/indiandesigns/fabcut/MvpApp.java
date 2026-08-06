package com.indiandesigns.fabcut;

import androidx.multidex.MultiDexApplication;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor.Level;
import com.indiandesigns.fabcut.di.component.ApplicationComponent;
import com.indiandesigns.fabcut.di.component.DaggerApplicationComponent;
import com.indiandesigns.fabcut.di.module.ApplicationModule;
import com.indiandesigns.fabcut.utils.AppLogger;
import com.indiandesigns.fabcut.utils.VersionInterceptor;

import okhttp3.OkHttpClient;

/**
 * Uses the {@link ApplicationComponent} (now prefixed with Dagger)
 * to inject our Application class.
 */

public class MvpApp extends MultiDexApplication {

    private ApplicationComponent mApplicationComponent;

    /**
     * Instantiates global {@link AppLogger}
     * Initializes AndroidNetworking and sets logging scope to BODY
     * when in DEBUG
     */

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

        AppLogger.init();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new VersionInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        if (BuildConfig.DEBUG) {
            AndroidNetworking.enableLogging(Level.BODY);
        }
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
