/**
 * 
 */
package com.osi.re.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class CheckpointResult implements Serializable {
	
	@Override
	public String toString() {
		return "CheckpointResult [resultId=" + resultId + ", value=" + value
				+ ", formatedValue=" + formatedValue
				+ ", checkpointExecutionTime=" + checkpointExecutionTime
				+ ", checkpointId=" + checkpointId + ", deviceId=" + deviceId
				+ ", checkType=" + checkType + ", processstatus="
				+ processstatus + ", threasholdValue=" + threasholdValue
				+ ", threasholdCondition=" + threasholdCondition
				+ ", recheckInterval=" + recheckInterval + ", repeatCount="
				+ repeatCount + ", dataType=" + dataType + ", connectType="
				+ connectType + ", resultDesc=" + resultDesc + ", assetName="
				+ assetName + ", errortype=" + errortype + ", pollid=" + pollid
				+ ", pollAuditCreationTime=" + pollAuditCreationTime + "]";
	}

	private int resultId;
	private String value;
	private String formatedValue;
	private Timestamp checkpointExecutionTime;
	private int checkpointId;
	private int deviceId;
	private char checkType;
	private char processstatus;
	private String threasholdValue;
	private String threasholdCondition;
	private int recheckInterval;
	private int repeatCount;
	private String dataType;
	private String connectType;
	private String resultDesc;
	private String assetName;
	private String errortype;
	private int pollid;
	private Timestamp pollAuditCreationTime;
	
	
	
	public CheckpointResult(){}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFormatedValue() {
		return formatedValue;
	}

	public void setFormatedValue(String formatedValue) {
		this.formatedValue = formatedValue;
	}

	public Timestamp getCheckpointExecutionTime() {
		return checkpointExecutionTime;
	}

	public void setCheckpointExecutionTime(Timestamp checkpointExecutionTime) {
		this.checkpointExecutionTime = checkpointExecutionTime;
	}

	public int getCheckpointId() {
		return checkpointId;
	}

	public void setCheckpointId(int checkpointId) {
		this.checkpointId = checkpointId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public char getCheckType() {
		return checkType;
	}

	public void setCheckType(char checkType) {
		this.checkType = checkType;
	}

	public char getProcessstatus() {
		return processstatus;
	}

	public void setProcessstatus(char processstatus) {
		this.processstatus = processstatus;
	}

	public String getThreasholdValue() {
		return threasholdValue;
	}

	public void setThreasholdValue(String threasholdValue) {
		this.threasholdValue = threasholdValue;
	}

	public String getThreasholdCondition() {
		return threasholdCondition;
	}

	public void setThreasholdCondition(String threasholdCondition) {
		this.threasholdCondition = threasholdCondition;
	}

	public int getRecheckInterval() {
		return recheckInterval;
	}

	public void setRecheckInterval(int recheckInterval) {
		this.recheckInterval = recheckInterval;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getConnectType() {
		return connectType;
	}

	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getErrorType() {
		return errortype;
	}

	public void setErrorType(String errortype) {
		this.errortype = errortype;
	}

	public int getPollid() {
		return pollid;
	}

	public void setPollid(int pollid) {
		this.pollid = pollid;
	}

	public Timestamp getPollAuditCreationTime() {
		return pollAuditCreationTime;
	}

	public void setPollAuditCreationTime(Timestamp pollAuditCreationTime) {
		this.pollAuditCreationTime = pollAuditCreationTime;
	}

	
}
