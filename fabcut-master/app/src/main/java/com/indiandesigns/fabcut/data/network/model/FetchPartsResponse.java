package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchPartsResponse {

    @SerializedName("ocNo")
    @Expose
    private String ocNo;

    @SerializedName("style")
    @Expose
    private String style;

    @SerializedName("parts")
    @Expose
    private List<FetchPartsDetails> parts;

    public FetchPartsResponse() {
    }

    public FetchPartsResponse(String ocNo, String style,List<FetchPartsDetails> parts) {
        this.ocNo = ocNo;
        this.style = style;
        this.parts = parts;
    }

    public String getOcNo() {
        return ocNo;
    }

    public void setOcNo(String ocNo) {
        this.ocNo = ocNo;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<FetchPartsDetails> getParts() {
        return parts;
    }

    public void setParts(List<FetchPartsDetails> parts) {
        this.parts = parts;
    }

}
