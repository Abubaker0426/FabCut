package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for IsDevRegResponse received from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class IsDevRegResponse extends ApiResponse implements Serializable {
    public Boolean getDevRegStatus() {
        return devRegStatus;
    }

    public void setDevRegStatus(Boolean devRegStatus) {
        this.devRegStatus = devRegStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @SerializedName("devRegStatus")
    private Boolean devRegStatus;

    @SerializedName("role")
    private String role;
}
