package com.indiandesigns.fabcut.di.module;

import android.app.Application;
import android.content.Context;
import com.indiandesigns.fabcut.data.AppDataManager;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.db.AppDbHelper;
import com.indiandesigns.fabcut.data.db.DbHelper;
import com.indiandesigns.fabcut.data.network.ApiHeader;
import com.indiandesigns.fabcut.data.network.ApiHelper;
import com.indiandesigns.fabcut.data.network.AppApiHelper;
import com.indiandesigns.fabcut.data.prefs.AppPreferencesHelper;
import com.indiandesigns.fabcut.data.prefs.PreferencesHelper;
import com.indiandesigns.fabcut.di.ApplicationContext;
import com.indiandesigns.fabcut.di.DatabaseInfo;
import com.indiandesigns.fabcut.di.PreferenceInfo;
import com.indiandesigns.fabcut.utils.AppConstants;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * Define classes and methods which provide dependencies
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    ApiHeader.ProtectedApiHeader provideProtectedApiHeader(PreferencesHelper preferencesHelper) {
        return new ApiHeader.ProtectedApiHeader(
                preferencesHelper.getAccessToken());
    }
}
