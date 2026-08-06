package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddPartStickersRequest {

    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("fitType")
    @Expose
    private String fitType;
    @SerializedName("lay")
    @Expose
    private Integer lay;
    @SerializedName("sizeDetails")
    @Expose
    private List<SizeDetail> sizeDetails = null;
    @SerializedName("jobId")
    @Expose
    private Long jobId;

    /**
     * No args constructor for use in serialization
     *
     */
    public AddPartStickersRequest() {
    }

    /**
     *
     * @param sizeDetails
     * @param lay
     * @param fitType
     */
    public AddPartStickersRequest(String location, String fitType, Integer lay, List<SizeDetail> sizeDetails, Long jobId) {
        super();
        this.location = location;
        this.fitType = fitType;
        this.lay = lay;
        this.sizeDetails = sizeDetails;
        this.jobId = jobId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFitType() {
        return fitType;
    }

    public void setFitType(String fitType) {
        this.fitType = fitType;
    }

    public Integer getLay() {
        return lay;
    }

    public void setLay(Integer lay) {
        this.lay = lay;
    }

    public List<SizeDetail> getSizeDetails() {
        return sizeDetails;
    }

    public void setSizeDetails(List<SizeDetail> sizeDetails) {
        this.sizeDetails = sizeDetails;
    }

    public Long getJobId() { return jobId; }

    public void setJobId(Long jobId) { this.jobId = jobId; }

}
