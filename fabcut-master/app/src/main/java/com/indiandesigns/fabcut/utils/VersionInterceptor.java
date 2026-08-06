package com.indiandesigns.fabcut.utils;

import android.util.Log;

import com.indiandesigns.fabcut.data.DataManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class VersionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String header = originalRequest.header("Version");
        if (header != null) {
            chain.proceed(originalRequest);
        }
        Request versionedRequest = originalRequest.newBuilder()
                                            .header("Version", "8.0")
                                            .build();
        return chain.proceed(versionedRequest);
    }
}
