package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemQuantityData implements Serializable {

    @SerializedName("cut")
    @Expose
    private Double cut;
    @SerializedName("assigned")
    @Expose
    private Double assigned;

    /**
     * No args constructor for use in serialization
     *
     */
    public ItemQuantityData() {
    }

    public ItemQuantityData(Double cut, Double assigned) {
        this.cut = cut;
        this.assigned = assigned;
    }

    public Double getCut() {
        return cut;
    }

    public void setCut(Double cut) {
        this.cut = cut;
    }

    public Double getAssigned() {
        return assigned;
    }

    public void setAssigned(Double assigned) {
        this.assigned = assigned;
    }
}
