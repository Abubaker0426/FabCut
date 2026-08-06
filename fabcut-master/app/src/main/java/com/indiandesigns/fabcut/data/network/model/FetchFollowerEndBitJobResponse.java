package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * Serializable POJO class for FetchFollowerEndBitJobResponse received from API
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

public class FetchFollowerEndBitJobResponse implements Serializable {

    @SerializedName("endBitJobId")
    @Expose
    private Integer endBitJobId;

    @SerializedName("ocNumber")
    @Expose
    private String ocNumber;

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
    public FetchFollowerEndBitJobResponse() {
    }


    /**
     *
     * @param endBitJobId
     * @param ocNumber
     * @param itemCode
     * @param itemDesc
     * @param jobDetails
     */
    public FetchFollowerEndBitJobResponse(Integer endBitJobId, String ocNumber, String itemCode, String itemDesc, List<JobDetail> jobDetails) {
        this.endBitJobId = endBitJobId;
        this.ocNumber = ocNumber;
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
        this.jobDetails = jobDetails;
    }

    public Integer getEndBitJobId() { return endBitJobId; }

    public void setEndBitJobId(Integer endBitJobId) { this.endBitJobId = endBitJobId; }

    public String getOcNumber() { return ocNumber; }

    public void setOcNumber(String ocNumber) { this.ocNumber = ocNumber; }

    public String getItemCode() { return itemCode; }

    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemDesc() { return itemDesc; }

    public void setItemDesc(String itemDesc) { this.itemDesc = itemDesc; }

    public List<JobDetail> getJobDetails() { return jobDetails; }

    public void setJobDetails(List<JobDetail> jobDetails) { this.jobDetails = jobDetails; }

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
         *
         * @param partName
         * @param layLength
         * @param ratioDetails
         */
        public JobDetail(String partName, Double layLength, List<RatioDetail> ratioDetails) {
            super();
            this.partName = partName;
            this.layLength = layLength;
            this.ratioDetails = ratioDetails;
        }

        public String getPartName() { return partName; }

        public void setPartName(String partName) { this.partName = partName; }

        public Double getLayLength() { return layLength; }

        public void setLayLength(Double layLength) { this.layLength = layLength; }

        public List<RatioDetail> getRatioDetails() { return ratioDetails; }

        public void setRatioDetails(List<RatioDetail> ratioDetails) { this.ratioDetails = ratioDetails; }

    }

}
