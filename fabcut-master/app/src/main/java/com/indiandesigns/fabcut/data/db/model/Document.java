package com.indiandesigns.fabcut.data.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

/**
 * Serializable POJO class for Document generated for print
 *
 * @SerializedName Defines the name to be used when
 * Serializing and Deserializing
 * @Expose To decide whether the variable will be exposed for
 * Serialization and Deserialization
 */

@Entity(nameInDb = "document")
public class Document {

    @Expose
    @SerializedName("id")
    @Id(autoincrement = true)
    private Long id;

    @SerializedName("name")
    @Expose
    @Property(nameInDb = "name")
    private String name;

    @SerializedName("jobId")
    @Expose
    @Property(nameInDb = "jobId")
    private Long jobId;

    @SerializedName("created_on")
    @Expose
    @Property(nameInDb = "created_on")
    private Date createdOn;

    @Generated(hash = 338553266)
    public Document(Long id, String name, Long jobId, Date createdOn) {
        this.id = id;
        this.name = name;
        this.jobId = jobId;
        this.createdOn = createdOn;
    }

    @Generated(hash = 91234483)
    public Document() {
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}