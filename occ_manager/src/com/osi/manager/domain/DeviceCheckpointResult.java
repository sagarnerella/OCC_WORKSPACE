package com.osi.manager.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checkpointResult")
public class DeviceCheckpointResult implements Serializable{
	private Integer deviceId;
	private Integer checkpointId;
	private String value;
	private Long checkpointExecutionTime;
	private Character checkType;
	private Character status;
	private int downtimeScheduled;
	private String connectType;
	private String resultDesc;
	private String formatedValue;
	
	public String getFormatedValue() {
		return formatedValue;
	}
	@XmlAttribute
	public void setFormatedValue(String formatedValue) {
		this.formatedValue = formatedValue;
	}

	public Integer getDeviceId() {
		return deviceId;
	}
	
	@XmlAttribute
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public Integer getCheckpointId() {
		return checkpointId;
	}
	
	@XmlAttribute
	public void setCheckpointId(Integer checkpointId) {
		this.checkpointId = checkpointId;
	}
	
	public String getValue() {
		return value;
	}
	
	@XmlAttribute
	public void setValue(String value) {
		this.value = value;
	}
	
	public Long getCheckpointExecutionTime() {
		return checkpointExecutionTime;
	}
	
	@XmlAttribute
	public void setCheckpointExecutionTime(Long checkpointExecutionTime) {
		this.checkpointExecutionTime = checkpointExecutionTime;
	}
	
	public Character getCheckType() {
		return checkType;
	}
	
	@XmlAttribute
	public void setCheckType(Character checkType) {
		this.checkType = checkType;
	}
	
	public Character getStatus() {
		return status;
	}
	
	@XmlAttribute
	public void setStatus(Character status) {
		this.status = status;
	}

	public int getDowntimeScheduled() {
		return downtimeScheduled;
	}
	@XmlAttribute
	public void setDowntimeScheduled(int downtimeScheduled) {
		this.downtimeScheduled = downtimeScheduled;
	}

	public String getConnectType() {
		return connectType;
	}
	@XmlAttribute
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}
	
	public String getResultDesc() {
		return resultDesc;
	}
	@XmlAttribute
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
}
