package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Serializable POJO class for ScanBarcodeResponse received from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class ScanBarcodeResponse {

    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("plies")
    @Expose
    private Double plies;

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getPlies() {
        return plies;
    }

    public void setPlies(Double plies) {
        this.plies = plies;
    }
}
