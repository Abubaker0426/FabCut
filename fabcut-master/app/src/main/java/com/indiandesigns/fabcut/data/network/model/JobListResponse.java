package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobListResponse {

    @SerializedName("jobId")
    @Expose
    private Long jobId;
    @SerializedName("tableNum")
    @Expose
    private Integer tableNum;
    @SerializedName("ocNo")
    @Expose
    private String ocNo;
    @SerializedName("fitType")
    @Expose
    private String fitType;
    @SerializedName("itemCode")
    @Expose
    private String itemCode;
    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("ratio")
    @Expose
    private Integer ratio;
    @SerializedName("lay")
    @Expose
    private Integer lay;

    /**
     * No args constructor for use in serialization
     *
     */
    public JobListResponse() {
    }

    /**
     *
     * @param jobId
     * @param quantity
     * @param size
     * @param lay
     * @param ocNo
     * @param itemCode
     * @param tableNum
     * @param fitType
     * @param itemDesc
     * @param ratio
     */
    public JobListResponse(Long jobId, Integer tableNum, String ocNo, String fitType, String itemCode, String itemDesc, String size, Integer quantity, Integer ratio, Integer lay) {
        super();
        this.jobId = jobId;
        this.tableNum = tableNum;
        this.ocNo = ocNo;
        this.fitType = fitType;
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
        this.size = size;
        this.quantity = quantity;
        this.ratio = ratio;
        this.lay = lay;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public void setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
    }

    public String getOcNo() {
        return ocNo;
    }

    public void setOcNo(String ocNo) {
        this.ocNo = ocNo;
    }

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
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

    public Integer getLay() {
        return lay;
    }

    public void setLay(Integer lay) {
        this.lay = lay;
    }

}