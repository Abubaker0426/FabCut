package com.indiandesigns.fabcut.data.network.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for DeleteEndBitJobRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */
public class EndBitJobRequest {

    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    @SerializedName("location")
    @Expose
    private String location;

    /**
     * No args constructor for use in serialization
     *
     */
    public EndBitJobRequest() {
    }

    public EndBitJobRequest(String deviceId, String location) {
        this.deviceId = deviceId;
        this.location = location;
    }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }
}
