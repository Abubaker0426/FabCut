package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for successful RESPONSE returned from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class ApiResponse implements Serializable {

    @Expose
    @SerializedName("result")
    private boolean result;

    @Expose
    @SerializedName("message")
    private String message;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
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

        ApiResponse that = (ApiResponse) o;

        if (result != that.result) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result1 = (result ? 1 : 0);
        result1 = 31 * result1 + (message != null ? message.hashCode() : 0);
        return result1;
    }
}
