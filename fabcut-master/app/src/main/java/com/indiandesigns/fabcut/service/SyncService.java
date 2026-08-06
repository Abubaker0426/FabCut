package com.indiandesigns.fabcut.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.indiandesigns.fabcut.MvpApp;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.di.component.DaggerServiceComponent;
import com.indiandesigns.fabcut.di.component.ServiceComponent;
import com.indiandesigns.fabcut.utils.AppLogger;
import javax.inject.Inject;

/**
 * Defines an application wide service to perform certain actions
 *
 * Not registered right now in Manifest.xml
 * Not in use currently
 */

public class SyncService extends Service {

    private static final String TAG = "SyncService";

    /**
     * Dependency injected via variable
     */

    @Inject
    DataManager mDataManager;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SyncService.class);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SyncService.class);
        context.startService(starter);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, SyncService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceComponent component = DaggerServiceComponent.builder()
                .applicationComponent(((MvpApp) getApplication()).getComponent())
                .build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLogger.d(TAG, "SyncService started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        AppLogger.d(TAG, "SyncService stopped");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
