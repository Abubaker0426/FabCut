package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {

    @SerializedName("itemCode")
    @Expose
    private String itemCode;
    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;

    /**
     * No args constructor for use in serialization
     *
     */
    public Item() {
    }

    /**
     *
     * @param itemCode
     * @param itemDesc
     */
    public Item(String itemCode, String itemDesc) {
        super();
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
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

}
