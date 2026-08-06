package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for ERROR response returned from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class ApiError {
    @Expose
    @SerializedName("result")
    private String result;

    @Expose
    @SerializedName("message")
    private String message;

    public ApiError(String statusCode, String message) {
        this.result = statusCode;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiError apiError = (ApiError) o;

        if (result != null ? !result.equals(apiError.result) : apiError.result != null)
            return false;
        return message != null ? message.equals(apiError.message) : apiError.message == null;
    }

    @Override
    public int hashCode() {
        int result1 = result != null ? result.hashCode() : 0;
        result1 = 31 * result1 + (message != null ? message.hashCode() : 0);
        return result1;
    }
}
