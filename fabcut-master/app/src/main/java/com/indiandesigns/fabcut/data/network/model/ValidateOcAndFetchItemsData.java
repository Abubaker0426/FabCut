package com.indiandesigns.fabcut.data.network.model;

/**
 * Serializable POJO class for ValidateOcAndFetchItemsData received from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class ValidateOcAndFetchItemsData {
    private String itemCode;

    private String itemDesc;

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
