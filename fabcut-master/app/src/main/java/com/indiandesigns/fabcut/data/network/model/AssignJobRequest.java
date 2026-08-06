package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Serializable POJO class for AssignJobRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class AssignJobRequest {

    @SerializedName("markerUnique")
    @Expose
    private String markerUnique;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("layLength")
    @Expose
    private Double layLength;
    @SerializedName("jobDetails")
    @Expose
    private List<JobDetail> jobDetails = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public AssignJobRequest() {
    }

    /**
     *
     * @param markerUnique
     * @param location
     * @param jobDetails
     * @param layLength
     */
    public AssignJobRequest(String markerUnique, String location, Double layLength, List<JobDetail> jobDetails) {
        super();
        this.markerUnique = markerUnique;
        this.location = location;
        this.layLength = layLength;
        this.jobDetails = jobDetails;
    }

    public String getMarkerUnique() {
        return markerUnique;
    }

    public void setMarkerUnique(String markerUnique) {
        this.markerUnique = markerUnique;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLayLength() {
        return layLength;
    }

    public void setLayLength(Double layLength) {
        this.layLength = layLength;
    }

    public List<JobDetail> getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(List<JobDetail> jobDetails) {
        this.jobDetails = jobDetails;
    }

    public class JobDetail {

        @SerializedName("itemCode")
        @Expose
        private String itemCode;
        @SerializedName("itemDesc")
        @Expose
        private String itemDesc;
        @SerializedName("shade")
        @Expose
        private String shade;
        @SerializedName("shrinkage")
        @Expose
        private String shrinkage;
        @SerializedName("pattern")
        @Expose
        private String pattern;
        @SerializedName("ratioDetails")
        @Expose
        private List<RatioDetail> ratioDetails = null;

        /**
         * No args constructor for use in serialization
         *
         */
        public JobDetail() {
        }

        /**
         *
         * @param itemCode
         * @param itemDesc
         * @param ratioDetails
         */
        public JobDetail(String itemCode, String itemDesc, String shade, String shrinkage, String pattern, List<RatioDetail> ratioDetails) {
            super();
            this.itemCode = itemCode;
            this.itemDesc = itemDesc;
            this.shade = shade;
            this.shrinkage = shrinkage;
            this.ratioDetails = ratioDetails;
            this.pattern = pattern;
        }

        public String getShade() { return shade; }

        public void setShade(String shade) { this.shade = shade; }

        public String getShrinkage() { return shrinkage; }

        public void setShrinkage(String shrinkage) { this.shrinkage = shrinkage; }

        public String getPattern() { return pattern; }

        public void setPattern(String pattern) { this.pattern = pattern; }

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

        public List<RatioDetail> getRatioDetails() {
            return ratioDetails;
        }

        public void setRatioDetails(List<RatioDetail> ratioDetails) {
            this.ratioDetails = ratioDetails;
        }

    }

    public class RatioDetail {

        @SerializedName("size")
        @Expose
        private String size;
        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("ratio")
        @Expose
        private Integer ratio;

        /**
         * No args constructor for use in serialization
         *
         */
        public RatioDetail() {
        }

        /**
         *
         * @param quantity
         * @param size
         * @param ratio
         */
        public RatioDetail(String size, Integer quantity, Integer ratio) {
            super();
            this.size = size;
            this.quantity = quantity;
            this.ratio = ratio;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getRatio() {
            return ratio;
        }

        public void setRatio(Integer ratio) {
            this.ratio = ratio;
        }

    }

}
