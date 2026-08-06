package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for FetchJobDetailsRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class FetchJobDetailsRequest implements Serializable {

    @SerializedName("ocNo")
    private String ocNo;

    @SerializedName("itemNo")
    private String itemNo;

    public String getOcNo() {
        return ocNo;
    }

    public void setOcNo(String ocNo) {
        this.ocNo = ocNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }
}
