package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Serializable POJO class for a Marker
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class Marker {

    @SerializedName("markerUnique")
    @Expose
    private String markerUnique;
    @SerializedName("shrinkage")
    @Expose
    private String shrinkage;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Marker() {
    }

    /**
     *
     * @param markerUnique
     * @param shrinkage
     * @param items
     */
    public Marker(String markerUnique, String shrinkage, List<Item> items) {
        super();
        this.markerUnique = markerUnique;
        this.shrinkage = shrinkage;
        this.items = items;
    }

    public String getMarkerUnique() {
        return markerUnique;
    }

    public void setMarkerUnique(String markerUnique) {
        this.markerUnique = markerUnique;
    }

    public String getShrinkage() {
        return shrinkage;
    }

    public void setShrinkage(String shrinkage) {
        this.shrinkage = shrinkage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
