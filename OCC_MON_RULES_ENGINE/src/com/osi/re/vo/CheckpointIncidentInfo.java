package com.osi.re.vo;

import java.io.Serializable;

public class CheckpointIncidentInfo implements Serializable {
	private int checkpointAlertId;
	private int deviceId;
	private int deviceCheckpointId;
	private int thresholdExceededCounter;
	private String alertStatus;
	private int agentId;
	private int repeatCount;
	
	public CheckpointIncidentInfo(){}

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
}
