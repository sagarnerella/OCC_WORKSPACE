package com.osi.agent.vo;

import java.util.List;

public class DeviceCheckPointDetails {
	private List<CheckPointDetails> checkPointDetails;
	private List<Integer> deviceIds;
	
	public DeviceCheckPointDetails(){
		
	}

	public List<CheckPointDetails> getCheckPointDetails() {
		return checkPointDetails;
	}

	public void setCheckPointDetails(List<CheckPointDetails> checkPointDetails) {
		this.checkPointDetails = checkPointDetails;
	}

	public List<Integer> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}
	
}
