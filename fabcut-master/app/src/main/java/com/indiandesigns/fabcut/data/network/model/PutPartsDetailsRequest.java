package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PutPartsDetailsRequest {
    @SerializedName("parts")
    @Expose
    private List<FetchPartsDetails> parts;

    public PutPartsDetailsRequest(List<FetchPartsDetails> parts) {
        this.parts = parts;
    }

    public List<FetchPartsDetails> getParts() {
        return parts;
    }

    public void setParts(List<FetchPartsDetails> parts) {
        this.parts = parts;
    }
}
