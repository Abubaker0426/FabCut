package com.indiandesigns.fabcut.ui.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BundleDetail {
    @SerializedName("bundleQuantity")
    @Expose
    private int bundleQuantity;

    @SerializedName("bundleCountry")
    @Expose
    private String bundleCountry;

    public int getBundleQuantity() {
        return bundleQuantity;
    }

    public void setBundleQuantity(int bundleQuantity) {
        this.bundleQuantity = bundleQuantity;
    }

    public String getBundleCountry() {
        return bundleCountry;
    }

    public void setBundleCountry(String bundleCountry) {
        this.bundleCountry = bundleCountry;
    }
}
