package com.osi.manager.vo;

public class DeviceXml {
private int agentId;
private String deviceXml;
private String status;
private String jobName;
private int id;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getAgentId() {
	return agentId;
}
public void setAgentId(int agentId) {
	this.agentId = agentId;
}
public String getDeviceXml() {
	return deviceXml;
}
public void setDeviceXml(String deviceXml) {
	this.deviceXml = deviceXml;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getJobName() {
	return jobName;
}
public void setJobName(String jobName) {
	this.jobName = jobName;
}
}
