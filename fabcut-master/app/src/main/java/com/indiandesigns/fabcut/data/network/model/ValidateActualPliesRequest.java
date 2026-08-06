package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValidateActualPliesRequest {

    @SerializedName("actualPlies")
    @Expose
    private Double actualPlies;

    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    @SerializedName("location")
    @Expose
    private String location;

    public ValidateActualPliesRequest(Double actualPlies, String deviceId, String location) {
        this.actualPlies = actualPlies;
        this.deviceId = deviceId;
        this.location = location;
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public ValidateActualPliesRequest() {
    }

    public Double getActualPlies() {
        return actualPlies;
    }

    public void setActualPlies(Double actualPlies) {
        this.actualPlies = actualPlies;
    }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

}
