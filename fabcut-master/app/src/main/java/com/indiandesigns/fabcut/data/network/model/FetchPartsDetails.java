package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchPartsDetails {
    @SerializedName("partsUnique")
    @Expose
    private Long partsUnique;

    @SerializedName("part")
    @Expose
    private String part;

    @SerializedName("isSelected")
    @Expose
    private int isSelected;

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public Long getPartsUnique() {
        return partsUnique;
    }

    public void setPartsUnique(Long partsUnique) {
        this.partsUnique = partsUnique;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public FetchPartsDetails(Long partsUnique, String part, int isSelected) {
        this.partsUnique = partsUnique;
        this.part = part;
        this.isSelected = isSelected;
    }
}
