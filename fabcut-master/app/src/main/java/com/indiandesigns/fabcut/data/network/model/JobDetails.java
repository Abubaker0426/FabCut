package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Serializable POJO class for job details returned from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class JobDetails {

    @SerializedName("ratios")
    @Expose
    private List<Ratio> ratios = null;
    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;
    @SerializedName("custName")
    @Expose
    private String custName;
    @SerializedName("styleNo")
    @Expose
    private String styleNo;
    @SerializedName("sizeQtys")
    @Expose
    private List<SizeQuantity> sizeQuantities = null;
    @SerializedName("layLengths")
    @Expose
    private List<LayLength> layLengths = null;

    public List<Ratio> getRatios() {
        return ratios;
    }

    public void setRatios(List<Ratio> ratios) {
        this.ratios = ratios;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getStyleNo() {
        return styleNo;
    }

    public void setStyleNo(String styleNo) {
        this.styleNo = styleNo;
    }

    public List<SizeQuantity> getSizeQuantities() {
        return sizeQuantities;
    }

    public void setSizeQuantities(List<SizeQuantity> sizeQuantities) {
        this.sizeQuantities = sizeQuantities;
    }

    public List<LayLength> getLayLengths() {
        return layLengths;
    }

    public void setLayLengths(List<LayLength> layLengths) {
        this.layLengths = layLengths;
    }

}
