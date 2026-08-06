package com.indiandesigns.fabcut.ui.common.model;

import com.indiandesigns.fabcut.data.network.model.Item;

import java.io.Serializable;
import java.util.List;

public class MarkerItem implements Serializable {

    private String markerUnique;
    private String shrinkage;
    private List<Item> items;
    private String ocNumber;

    /**
     * No args constructor for use in serialization
     *
     */
    public MarkerItem() {
    }

    /**
     *
     * @param ocNumber
     * @param markerUnique
     * @param items
     * @param shrinkage
     */
    public MarkerItem(String markerUnique, String shrinkage, List<Item> items, String ocNumber) {
        super();
        this.markerUnique = markerUnique;
        this.shrinkage = shrinkage;
        this.items = items;
        this.ocNumber = ocNumber;
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

    public String getOcNumber() {
        return ocNumber;
    }

    public void setOcNumber(String ocNumber) {
        this.ocNumber = ocNumber;
    }

}