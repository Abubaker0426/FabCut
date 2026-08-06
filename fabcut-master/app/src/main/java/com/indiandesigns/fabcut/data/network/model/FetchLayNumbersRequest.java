package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for FetchLayNumbersRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class FetchLayNumbersRequest {

    @SerializedName("itemCode")
    @Expose
    private String itemCode;

    @SerializedName("location")
    @Expose
    private String location;

    public String getItemCode() { return itemCode; }

    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }
}
