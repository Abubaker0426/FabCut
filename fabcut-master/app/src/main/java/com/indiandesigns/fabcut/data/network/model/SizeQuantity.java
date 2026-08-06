package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for Size Quantity
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class SizeQuantity {

    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("completedQty")
    @Expose
    private Integer completedQty;

    public Integer getCompletedQty() {
        return completedQty;
    }

    public void setCompletedQty(Integer completedQty) {
        this.completedQty = completedQty;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

}