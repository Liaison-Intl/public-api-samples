/**
 * Entity representing an Organization in the Liaison UNICAS system.
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

public class Organization {

    private int id;
    private String name;
    private Date createdDate;
    private Date updatedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return new StringBuilder().append("Organization {id=").append(id)
                .append(", name='").append(name)
                .append("\', createdDate=").append(createdDate)
                .append(", updatedDate=").append(updatedDate).append('}')
                .toString();
    }
}
