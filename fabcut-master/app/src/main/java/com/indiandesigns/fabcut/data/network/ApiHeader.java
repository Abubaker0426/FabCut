package com.indiandesigns.fabcut.data.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides the Authorization Header to include in API calls
 */

@Singleton
public class ApiHeader {

    private ProtectedApiHeader mProtectedApiHeader;

    /**
     * Parameterized Constructor
     * Initializes the API Header
     *
     * @param protectedApiHeader Injected with Dagger
     */
    @Inject
    public ApiHeader(ProtectedApiHeader protectedApiHeader) {
        mProtectedApiHeader = protectedApiHeader;
    }

    /**
     * Fetches the API Header
     *
     * @return Instance of ProtectedApiHeader
     */
    public ProtectedApiHeader getProtectedApiHeader() {
        return mProtectedApiHeader;
    }

    /**
     * Serializable POJO class for Authorization Header
     *
     * @SerializedName Defines the name to be used when
     * Serializing and Deserializing
     * @Expose To decide whether the variable will be exposed for
     * Serialization and Deserialization
     */

    public static final class ProtectedApiHeader {

        @Expose
        @SerializedName("Authorization")
        private String mAccessToken;

        public ProtectedApiHeader(String mAccessToken) {
            this.mAccessToken = mAccessToken;
        }

        public String getAccessToken() {
            return mAccessToken;
        }

        public void setAccessToken(String accessToken) {
            mAccessToken = accessToken;
        }
    }
}
