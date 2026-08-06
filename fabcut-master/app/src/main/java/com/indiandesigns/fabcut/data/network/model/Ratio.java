package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for a Ratio
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class Ratio {

    @SerializedName("ratioNumber")
    @Expose
    private Integer ratioNumber;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("ratio")
    @Expose
    private Integer ratio;
    @SerializedName("ratioQty")
    @Expose
    private Integer ratioQty;

    public Integer getRatioNumber() {
        return ratioNumber;
    }

    public void setRatioNumber(Integer ratioNumber) {
        this.ratioNumber = ratioNumber;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }

    public Integer getRatioQty() {
        return ratioQty;
    }

    public void setRatioQty(Integer ratioQty) {
        this.ratioQty = ratioQty;
    }

}