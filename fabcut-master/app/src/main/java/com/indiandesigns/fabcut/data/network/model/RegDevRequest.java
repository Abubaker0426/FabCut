package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Serializable POJO class for RegDevRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class RegDevRequest implements Serializable {

    @SerializedName("location")
    private String location;

    @SerializedName("role")
    private String role;

    @SerializedName("tableNumber")
    private int tableNumber;

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
