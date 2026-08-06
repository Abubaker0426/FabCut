package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SizeDetailBarcodes {

    @SerializedName("partsUnique")
    @Expose
    private Long partsUnique;

    @SerializedName("part")
    @Expose
    private String part;

    @SerializedName("barcode")
    @Expose
    private String barcode;

    public SizeDetailBarcodes(Long partsUnique, String part, String barcode) {
        this.partsUnique = partsUnique;
        this.part = part;
        this.barcode = barcode;
    }

    public Long getPartsUnique() { return partsUnique; }

    public void setPartsUnique(Long partsUnique) { this.partsUnique = partsUnique; }

    public String getPart() { return part; }

    public void setPart(String part) { this.part = part; }

    public String getBarcode() { return barcode; }

    public void setBarcode(String barcode) { this.barcode = barcode; }

}
