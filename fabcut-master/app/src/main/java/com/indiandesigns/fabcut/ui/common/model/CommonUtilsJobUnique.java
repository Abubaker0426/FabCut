package com.indiandesigns.fabcut.ui.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonUtilsJobUnique {

    @SerializedName("ocNo")
    @Expose
    private String ocNo;

    @SerializedName("lay")
    @Expose
    private Integer lay;

    @SerializedName("fitType")
    @Expose
    private String fitType;


    public CommonUtilsJobUnique(String ocNo, Integer lay, String fitType, String itemCode) {
        this.ocNo = ocNo;
        this.lay = lay;
        this.fitType = fitType;
        this.itemCode = itemCode;
    }

    public String getOcNo() {
        return ocNo;
    }

    public void setOcNo(String ocNo) {
        this.ocNo = ocNo;
    }

    public Integer getLay() {
        return lay;
    }

    public void setLay(Integer lay) {
        this.lay = lay;
    }

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @SerializedName("itemCode")
    @Expose
    private String itemCode;
}
