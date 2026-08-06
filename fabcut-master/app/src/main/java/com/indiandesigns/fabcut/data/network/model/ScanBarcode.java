package com.indiandesigns.fabcut.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Serializable POJO class for Scanned Barcode on follower job page
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

@Entity(nameInDb = "barcodes")
public class ScanBarcode {

    @Expose
    @SerializedName("id")
    @Id(autoincrement = true)
    private Long id;

    @SerializedName("barcode")
    @Expose
    @Property(nameInDb = "barcode")
    private String barcode;
    @SerializedName("expected_plies")
    @Expose
    @Property(nameInDb = "expected_plies")
    private Double expectedPlies;
    @SerializedName("actual_plies")
    @Expose
    @Property(nameInDb = "actual_plies")
    private Double actualPlies;
    @SerializedName("reason")
    @Expose
    @Property(nameInDb = "reason")
    private String reason;
    @SerializedName("jobId")
    @Expose
    @Property(nameInDb = "jobId")
    private Integer jobId;
    @SerializedName("validated")
    @Expose
    @Property(nameInDb = "validated")
    private Boolean validated;
    @SerializedName("item_code")
    @Expose
    @Property(nameInDb = "item_code")
    private String itemCode;

    @Generated(hash = 1178530178)
    public ScanBarcode(Long id, String barcode, Double expectedPlies, Double actualPlies, String reason, Integer jobId, Boolean validated, String itemCode) {
        this.id = id;
        this.barcode = barcode;
        this.expectedPlies = expectedPlies;
        this.actualPlies = actualPlies;
        this.reason = reason;
        this.jobId = jobId;
        this.validated = validated;
        this.itemCode = itemCode;
    }

    @Generated(hash = 1790509930)
    public ScanBarcode() {
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getExpectedPlies() {
        return expectedPlies;
    }

    public void setExpectedPlies(Double expectedPlies) {
        this.expectedPlies = expectedPlies;
    }

    public Double getActualPlies() {
        return actualPlies;
    }

    public void setActualPlies(Double actualPlies) {
        this.actualPlies = actualPlies;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
