package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for FetchMarkersRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */


public class FetchMarkersRequest {

    @SerializedName("location")
    private String location;

    @SerializedName("fitType")
    private String fitType;

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
