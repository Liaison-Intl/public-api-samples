/**
 * Entity representing an Application Submission in the Liaison UNICAS system.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.liaisonedu.util.TimestampDeserializer;
import com.liaisonedu.util.TimestampSerializer;

public class ApplicationSubmission {

    private long applicationId;
    private String applicantFirstName;
    private String applicantLastName;
    private long casApplicantId;
    private Date createdDate;
    private Date updatedDate;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public long getCasApplicantId() {
        return casApplicantId;
    }

    public void setCasApplicantId(long casApplicantId) {
        this.casApplicantId = casApplicantId;
    }

    @JsonSerialize(using = TimestampSerializer.class)
    public Date getCreatedDate() {
        return createdDate;
    }

    @JsonDeserialize(using = TimestampDeserializer.class)
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonSerialize(using = TimestampSerializer.class)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @JsonDeserialize(using = TimestampDeserializer.class)
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
