package com.indiandesigns.fabcut.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.indiandesigns.fabcut.R;

public final class AppUtils {

    private AppUtils() {
    }

    /**
     * Opens Google Play Store for provided package id
     *
     * @param context Context
     */

    public static void openPlayStoreForApp(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context
                            .getResources()
                            .getString(R.string.app_market_link) + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context
                            .getResources()
                            .getString(R.string.app_google_play_store_link) + appPackageName)));
        }
    }

}
