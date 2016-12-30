package com.osi.re.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class CheckpointAlert implements Serializable  {
	private int checkpointAlertId;
	private int deviceId;
	private int deviceCheckpointId;
	private int thresholdExceededCounter;
	private String alertStatus;
	private int agentId;
	private int repeatCount;
	private int assetId;
	private String AssetName;
	private int masterCheckpointId;
	private int resultId;
	private String resultDesc;
	private Timestamp checkpointExecutionTime;
	
	public CheckpointAlert(){}

	public int getCheckpointAlertId() {
		return checkpointAlertId;
	}

	public void setCheckpointAlertId(int checkpointAlertId) {
		this.checkpointAlertId = checkpointAlertId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getDeviceCheckpointId() {
		return deviceCheckpointId;
	}

	public void setDeviceCheckpointId(int deviceCheckpointId) {
		this.deviceCheckpointId = deviceCheckpointId;
	}

	public int getThresholdExceededCounter() {
		return thresholdExceededCounter;
	}

	public void setThresholdExceededCounter(int thresholdExceededCounter) {
		this.thresholdExceededCounter = thresholdExceededCounter;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public int getAssetId() {
		return assetId;
	}

	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}

	public String getAssetName() {
		return AssetName;
	}

	public void setAssetName(String assetName) {
		AssetName = assetName;
	}

	public int getMasterCheckpointId() {
		return masterCheckpointId;
	}

	public void setMasterCheckpointId(int masterCheckpointId) {
		this.masterCheckpointId = masterCheckpointId;
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public Timestamp getCheckpointExecutionTime() {
		return checkpointExecutionTime;
	}

	public void setCheckpointExecutionTime(Timestamp checkpointExecutionTime) {
		this.checkpointExecutionTime = checkpointExecutionTime;
	}
}
