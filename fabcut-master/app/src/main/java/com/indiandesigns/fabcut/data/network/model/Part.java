package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Part {

    @SerializedName("partName")
    @Expose
    private String partName;
    @SerializedName("barcode")
    @Expose
    private String barcode;

    /**
     * No args constructor for use in serialization
     *
     */
    public Part() {
    }

    /**
     *
     * @param barcode
     * @param partName
     */
    public Part(String partName, String barcode) {
        super();
        this.partName = partName;
        this.barcode = barcode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
