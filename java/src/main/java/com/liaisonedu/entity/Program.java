/**
 * Entity representing a Program in the Liaison UNICAS system.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liaisonedu.util.DateDeserializer;
import com.liaisonedu.util.DateSerializer;
import com.liaisonedu.util.TimestampDeserializer;
import com.liaisonedu.util.TimestampSerializer;

public class Program {

    private int id;
    private String code;
    private String name;
    private String status;
    private String webadmitName;
    private Date startDate;
    private Date deadline;
    private Date createdDate;
    private Date updatedDate;
    private String academicYear;
    private String type;
    private String level;
    private String department;
    private String track;
    private String startTerm;
    private String delivery;
    private int fee;
    private String city;
    private String state;
    private String zipCode;
    private String campus;
    private String deadlineDisplay;
    private String applicationType;
    private String concentration;
    private Long uniqueIdentifier;
    private String uniqueIdentifierString;
    private Long orgId;
    private String orgName;
    private String orgCode;
    private Boolean earlyDecisionEnable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWebadmitName() {
        return webadmitName;
    }

    public void setWebadmitName(String webadmitName) {
        this.webadmitName = webadmitName;
    }

	@JsonSerialize(using = DateSerializer.class)
    public Date getStartDate() {
        return startDate;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

	@JsonSerialize(using = DateSerializer.class)
    public Date getDeadline() {
        return deadline;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getStartTerm() {
        return startTerm;
    }

    public void setStartTerm(String startTerm) {
        this.startTerm = startTerm;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getDeadlineDisplay() {
        return deadlineDisplay;
    }

    public void setDeadlineDisplay(String deadlineDisplay) {
        this.deadlineDisplay = deadlineDisplay;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public Long getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(Long uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getUniqueIdentifierString() {
        return uniqueIdentifierString;
    }

    public void setUniqueIdentifierString(String uniqueIdentifierString) {
        this.uniqueIdentifierString = uniqueIdentifierString;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Boolean getEarlyDecisionEnable() {
        return earlyDecisionEnable;
    }

    public void setEarlyDecisionEnable(Boolean earlyDecisionEnable) {
        this.earlyDecisionEnable = earlyDecisionEnable;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Program {id=").append(id)
                .append(", code='").append(code)
                .append("\', name='").append(name)
                .append("\', status='").append(status)
                .append("\', webadmitName='").append(webadmitName)
                .append("\', startDate=").append(startDate)
                .append(", deadline=").append(deadline)
                .append(", createdDate=").append(createdDate)
                .append(", updatedDate=").append(updatedDate)
                .append(", academicYear='").append(academicYear)
                .append("\', type='").append(type)
                .append("\', level='").append(level)
                .append("\', department='").append(department)
                .append("\', track='").append(track)
                .append("\', startTerm='").append(startTerm)
                .append("\', delivery='").append(delivery)
                .append("\', fee=").append(fee)
                .append(", city='").append(city)
                .append("\', state='").append(state)
                .append("\', zipCode='").append(zipCode)
                .append("\', campus='").append(campus)
                .append("\', deadlineDisplay='").append(deadlineDisplay)
                .append("\', applicationType='").append(applicationType)
                .append("\', uniqueIdentifier='").append(uniqueIdentifier)
                .append("\', uniqueIdentifierString='").append(uniqueIdentifierString)
                .append("\', orgId='").append(orgId).append("\'}")
                .append("\', orgName='").append(orgName).append("\'}")
                .append("\', earlyDecisionEnable='").append(earlyDecisionEnable).append("\'}")
                .append("\', orgCode='").append(orgCode).append("\'}")
                .append("\', concentration='").append(concentration).append("\'}")
                .toString();
    }
}
