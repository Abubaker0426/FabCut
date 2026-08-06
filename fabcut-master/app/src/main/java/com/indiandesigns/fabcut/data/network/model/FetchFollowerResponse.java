package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable POJO class for FetchFollowerResponse received from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class FetchFollowerResponse implements Serializable {

    @SerializedName("jobId")
    @Expose
    private Integer jobId;
    @SerializedName("ocNumber")
    @Expose
    private String ocNumber;
    @SerializedName("layLength")
    @Expose
    private Double layLength;
    @SerializedName("fitType")
    @Expose
    private String fitType;
    @SerializedName("jobDetails")
    @Expose
    private List<JobDetail> jobDetails = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public FetchFollowerResponse() {
    }

    /**
     *
     * @param jobId
     * @param ocNumber
     * @param jobDetails
     * @param layLength
     * @param fitType
     */
    public FetchFollowerResponse(Integer jobId, String ocNumber, Double layLength, String fitType, List<JobDetail> jobDetails) {
        super();
        this.jobId = jobId;
        this.ocNumber = ocNumber;
        this.layLength = layLength;
        this.fitType = fitType;
        this.jobDetails = jobDetails;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getOcNumber() {
        return ocNumber;
    }

    public void setOcNumber(String ocNumber) {
        this.ocNumber = ocNumber;
    }

    public Double getLayLength() {
        return layLength;
    }

    public void setLayLength(Double layLength) {
        this.layLength = layLength;
    }

    public String getFitType() { return fitType; }

    public void setFitType(String fitType) { this.fitType = fitType; }

    public List<JobDetail> getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(List<JobDetail> jobDetails) {
        this.jobDetails = jobDetails;
    }

    public class RatioDetail implements Serializable {

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

    public class JobDetail implements Serializable {

        @SerializedName("itemCode")
        @Expose
        private String itemCode;
        @SerializedName("itemDesc")
        @Expose
        private String itemDesc;
        @SerializedName("layNumber")
        @Expose
        private Integer layNumber;
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
         * @param ratioDetails
         * @param itemDesc
         * @param layNumber
         */
        public JobDetail(String itemCode, String itemDesc, Integer layNumber, List<RatioDetail> ratioDetails) {
            super();
            this.itemCode = itemCode;
            this.itemDesc = itemDesc;
            this.layNumber = layNumber;
            this.ratioDetails = ratioDetails;
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

        public Integer getLayNumber() { return layNumber; }

        public void setLayNumber(Integer layNumber) { this.layNumber = layNumber; }

        public List<RatioDetail> getRatioDetails() {
            return ratioDetails;
        }

        public void setRatioDetails(List<RatioDetail> ratioDetails) {
            this.ratioDetails = ratioDetails;
        }

    }

}
