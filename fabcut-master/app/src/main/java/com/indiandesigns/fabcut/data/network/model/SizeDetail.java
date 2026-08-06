package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SizeDetail {

    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;
    @SerializedName("itemCode")
    @Expose
    private String itemCode;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("augmentedSize")
    @Expose
    private  String augmentedSize;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("countryName")
    @Expose
    private String countryName;

    @SerializedName("barcodeDetails")
    @Expose
    private List<SizeDetailBarcodes> barcodeDetails = null;

    @SerializedName("bundleNumber")
    @Expose
    private Integer bundleNumber;

    /**
     * No args constructor for use in serialization
     *
     */
    public SizeDetail() {
    }


    public SizeDetail(String itemDesc, String itemCode, String size, String augmentedSize, String countryCode, String countryName,
                      Integer quantity, Integer bundleNumber, List<SizeDetailBarcodes> barcodeDetails) {
        super();
        this.itemDesc = itemDesc;
        this.itemCode = itemCode;
        this.size = size;
        this.augmentedSize = augmentedSize;
        this.quantity = quantity;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.barcodeDetails = barcodeDetails;
        this.bundleNumber = bundleNumber;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAugmentedSize() { return augmentedSize; }

    public void setAugmentedSize(String augmentedSize) { this.augmentedSize = augmentedSize; }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCountryCode() { return countryCode; }

    public void setCountryCode(String countryCode) { this.countryCode = countryCode;}

    public String getCountryName() { return countryName; }

    public void setCountryName(String countryName) { this.countryName = countryName; }

    public List<SizeDetailBarcodes> getBarcodeDetails() { return barcodeDetails; }

    public void setBarcodeDetails(List<SizeDetailBarcodes> barcodeDetails) { this.barcodeDetails = barcodeDetails; }

    public Integer getBundleNumber() { return bundleNumber; }

    public void setBundleNumber(Integer bundleNumber) { this.bundleNumber = bundleNumber; }
}
