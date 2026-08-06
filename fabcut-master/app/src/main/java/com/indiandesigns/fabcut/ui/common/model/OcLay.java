package com.indiandesigns.fabcut.ui.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OcLay {

    @SerializedName("jobId")
    @Expose
    private Long jobId;

    @SerializedName("tableNum")
    @Expose
    private Integer tableNum;

    @SerializedName("ocNo")
    @Expose
    private String ocNo;

    @SerializedName("lay")
    @Expose
    private Integer lay;

    @SerializedName("fitType")
    @Expose
    private String fitType;

    @SerializedName("sizeList")
    @Expose
    private List<OcLayDetail> sizeList;


    public OcLay() {
    }

    public OcLay(Long jobId, Integer tableNum, String ocNo, Integer lay, String fitType) {
        this.jobId = jobId;
        this.tableNum = tableNum;
        this.ocNo = ocNo;
        this.lay = lay;
        this.fitType = fitType;
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

    public Integer getLay() {
        return lay;
    }

    public void setLay(Integer lay) {
        this.lay = lay;
    }

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
    }


    public List<OcLayDetail> getSizeList() { return sizeList; }

    public void setSizeList(List<OcLayDetail> sizeList) {
        this.sizeList = sizeList;
    }

}
