package com.indiandesigns.fabcut.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

/**
 * This class does the following
 * - Checks for network connectivity
 */

public final class NetworkUtils {

    private NetworkUtils() {
        // This utility class is not publicly instantiable
    }

    /**
     * Checks for internet connection and returns a boolean based on the result
     *
     * @param context Context of where it is being called from
     * @return true if network is available and connected
     */

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
