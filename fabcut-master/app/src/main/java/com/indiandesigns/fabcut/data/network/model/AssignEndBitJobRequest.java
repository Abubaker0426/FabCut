package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Serializable POJO class for AssignEndBitJobRequest sent to API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class AssignEndBitJobRequest {

    @SerializedName("markerId")
    @Expose
    private String markerId;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("layNumber")
    @Expose
    private Integer layNumber;

    @SerializedName("itemCode")
    @Expose
    private String itemCode;

    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;

    @SerializedName("jobDetails")
    @Expose
    private List<JobDetail> jobDetails = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public AssignEndBitJobRequest() {
    }

    /**
     *
     * @param markerId
     * @param location
     * @param jobDetails
     * @param layNumber
     * @param itemCode
     * @param itemDesc
     */
    public AssignEndBitJobRequest(String markerId, String location, Integer layNumber, String itemCode, String itemDesc, List<JobDetail> jobDetails) {
        super();
        this.markerId = markerId;
        this.location = location;
        this.layNumber = layNumber;
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
        this.jobDetails = jobDetails;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getLayNumber() { return layNumber; }

    public void setLayNumber(Integer layNumber) { this.layNumber = layNumber; }

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

    public List<JobDetail> getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(List<JobDetail> jobDetails) {
        this.jobDetails = jobDetails;
    }

    public class JobDetail {

        @SerializedName("partName")
        @Expose
        private String partName;

        @SerializedName("layLength")
        @Expose
        private Double layLength;

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
         * @param partName
         * @param ratioDetails
         * @param layLength
         */
        public JobDetail( String partName, Double layLength, List<RatioDetail> ratioDetails) {
            super();
            this.partName = partName;
            this.layLength = layLength;
            this.ratioDetails = ratioDetails;
        }

        public String getPartName() { return partName; }

        public void setPartName(String partName) { this.partName = partName; }

        public Double getLayLength() {
            return layLength;
        }

        public void setLayLength(Double layLength) {
            this.layLength = layLength;
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
         */
        public RatioDetail(String size, Integer quantity) {
            super();
            this.size = size;
            this.quantity = quantity;
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

    }

}
