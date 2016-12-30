package com.osi.re.vo;

import java.io.Serializable;

public class CheckPointDetails implements Serializable {
	private int checkpointID;
	private String hostName;
	private String ipAddress;
	private int deviceID;
	private int port;
	private String connectType;
	private String communityString;
	private int snmpVersion;
	private String checkPoint;
	private int frequency;
	private String dataType;
	private char checkType;
	private int repeatCount;

	public CheckPointDetails(){}

	public int getCheckpointID() {
		return checkpointID;
	}

	public void setCheckpointID(int checkpointID) {
		this.checkpointID = checkpointID;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getConnectType() {
		return connectType;
	}

	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}

	public String getCommunityString() {
		return communityString;
	}

	public void setCommunityString(String communityString) {
		this.communityString = communityString;
	}

	public int getSnmpVersion() {
		return snmpVersion;
	}

	public void setSnmpVersion(int snmpVersion) {
		this.snmpVersion = snmpVersion;
	}

	public String getCheckPoint() {
		return checkPoint;
	}

	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public char getCheckType() {
		return checkType;
	}

	public void setCheckType(char checkType) {
		this.checkType = checkType;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
	
	
	public boolean isValid(CheckPointDetails checkPointDetails) {
		if(checkPointDetails.getIpAddress() != null && !"".equals(checkPointDetails.getIpAddress())){
		}else{
			return false;
		}
		if(checkPointDetails.getPort() > 0  && !"".equals(checkPointDetails.getPort())){
		}else{
			return false;
		}
		if(checkPointDetails.getCheckPoint() != null && !"".equals(checkPointDetails.getCheckPoint())){
		}else{
			return false;
		}
		if(checkPointDetails.getCommunityString() != null && !"".equals(checkPointDetails.getCommunityString())){
		}else{
			return false;
		}
		if(checkPointDetails.getRepeatCount() >0 && !"".equals(checkPointDetails.getRepeatCount())){
		}else{
			return false;
		}
		if(checkPointDetails.getFrequency() >0 && !"".equals(checkPointDetails.getFrequency())){
		}else{
			return false;
		}
		if(checkPointDetails.getSnmpVersion() >0 && !"".equals(checkPointDetails.getSnmpVersion())){
		}else{
			return false;
		}
		return true;
	}
}
