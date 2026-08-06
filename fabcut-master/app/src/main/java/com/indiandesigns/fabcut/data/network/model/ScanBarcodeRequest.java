package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for ScanBarcodeRequest made to the API
 */
public class ScanBarcodeRequest implements Serializable {

    @SerializedName("itemCode")
    private String itemCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}