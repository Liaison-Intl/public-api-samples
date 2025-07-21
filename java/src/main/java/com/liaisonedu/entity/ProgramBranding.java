/**
 * Entity representing Program Branding information.
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

public class ProgramBranding {

	private String configName;
	private String configValue;
	private Date createdDate;
	private Date updatedDate;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
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
