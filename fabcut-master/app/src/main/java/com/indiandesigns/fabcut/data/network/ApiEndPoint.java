package com.indiandesigns.fabcut.data.network;

import com.indiandesigns.fabcut.BuildConfig;

public final class ApiEndPoint {

    public static final String REGISTRATIONS = BuildConfig.BASE_URL
            + "/fabcut/cutting/registrations";

    public static final String VALIDATE_LOCATION = BuildConfig.BASE_URL
            + "/service/validate/locations";

    public static final String RATIOS = BuildConfig.BASE_URL
            + "/fabcut/cutting/ratios";

    public static final String FOLLOWERS = BuildConfig.BASE_URL
            + "/fabcut/cutting/followers";

    public static final String JOBS = BuildConfig.BASE_URL
            + "/fabcut/cutting/jobs";

    public static final String END_BITS = BuildConfig.BASE_URL
            + "/fabcut/cutting/end-bits";

    public static final String PARTS = BuildConfig.BASE_URL
            + "/fabcut/cutting/parts";

    private ApiEndPoint() {
        // This class is not publicly instantiable
    }

}
