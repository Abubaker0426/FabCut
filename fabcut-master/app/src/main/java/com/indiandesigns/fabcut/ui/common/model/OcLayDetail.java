package com.indiandesigns.fabcut.ui.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OcLayDetail {

    @SerializedName("size")
    @Expose
    private String size;

    @SerializedName("augmentedSize")
    @Expose
    private String augmentedSize;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    @SerializedName("ratio")
    @Expose
    private Integer ratio;

    @SerializedName("fitType")
    @Expose
    private String fitType;

    @SerializedName("itemCode")
    @Expose
    private String itemCode;

    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;

    @SerializedName("totalQuantity")
    @Expose
    private Integer totalQuantity;

    @SerializedName("bundleName")
    @Expose
    private String bundleName;

    @SerializedName("jobId")
    @Expose
    private Long jobId;

    @SerializedName("bundleNumber")
    @Expose
    private int bundleNumber;

    public OcLayDetail() {
    }


    public OcLayDetail(String size, String augmentedSize, String country, Integer quantity, Integer ratio, String fitType, String itemCode, String itemDesc, Integer totalQuantity, String bundleName, Long jobId) {
        this.size = size;
        this.augmentedSize = augmentedSize;
        this.country = country;
        this.quantity = quantity;
        this.ratio = ratio;
        this.fitType = fitType;
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
        this.totalQuantity = totalQuantity;
        this.bundleName = bundleName;
        this.jobId = jobId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCountry() {return country; }

    public void setCountry(String country) {this.country = country;}

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
    }

    public String getAugmentedSize() { return augmentedSize; }

    public void setAugmentedSize(String augmentedSize) { this.augmentedSize = augmentedSize; }

    public Integer getTotalQuantity() { return totalQuantity; }

    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public String getBundleName() { return bundleName; }

    public void setBundleName(String bundleName) { this.bundleName = bundleName; }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public int getBundleNumber() { return bundleNumber; }

    public void setBundleNumber(int bundleNumber) { this.bundleNumber = bundleNumber; }

}
