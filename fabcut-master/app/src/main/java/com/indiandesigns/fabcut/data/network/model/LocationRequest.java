package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for a Follower
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class LocationRequest {


    @SerializedName("location")
    @Expose
    private String location;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}