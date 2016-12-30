package com.osi.agent.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "logMiner")
public class LogMiner {
	private String hostName;
	private String ipAddress;
	private String connectType;
	private String userName;
	private String password;
	private Integer port;
	private List<Checkpoint> checkpoint;
	
	public LogMiner(){}

	public String getHostName() {
		return hostName;
	}
	@XmlAttribute
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	@XmlAttribute
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getConnectType() {
		return connectType;
	}
	@XmlAttribute
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}

	public String getUserName() {
		return userName;
	}
	@XmlAttribute
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}
	@XmlAttribute
	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPort() {
		return port;
	}
	@XmlAttribute
	public void setPort(Integer port) {
		this.port = port;
	}

	public List<Checkpoint> getCheckpoint() {
		return checkpoint;
	}
	@XmlElement
	public void setCheckpoint(List<Checkpoint> checkpoint) {
		this.checkpoint = checkpoint;
	}
}
