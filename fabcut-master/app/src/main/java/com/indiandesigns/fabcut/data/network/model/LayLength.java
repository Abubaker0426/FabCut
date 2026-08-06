package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for Lay Length for ratio
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class LayLength {

    @SerializedName("layLength")
    @Expose
    private Double layLength;
    @SerializedName("ratioNumber")
    @Expose
    private Integer ratioNumber;

    public Double getLayLength() {
        return layLength;
    }

    public void setLayLength(Double layLength) {
        this.layLength = layLength;
    }

    public Integer getRatioNumber() {
        return ratioNumber;
    }

    public void setRatioNumber(Integer ratioNumber) {
        this.ratioNumber = ratioNumber;
    }

}