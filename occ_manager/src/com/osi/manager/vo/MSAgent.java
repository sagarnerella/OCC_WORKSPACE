package com.osi.manager.vo;

public class MSAgent {
	private Integer agentId;
	private String agentName;
	private Integer deviceId;
	private String ipAddress;
	private String hostName;
	private Integer port;
	private boolean executionFlag;
	
	
	public MSAgent(){
		
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isExecutionFlag() {
		return executionFlag;
	}

	public void setExecutionFlag(boolean executionFlag) {
		this.executionFlag = executionFlag;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
}
