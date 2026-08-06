package com.indiandesigns.fabcut.data.network.enums;

import com.google.gson.annotations.SerializedName;

public enum Status {

    @SerializedName("Idle")
    IDLE("Idle"),
    @SerializedName("Busy")
    BUSY("Busy");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
