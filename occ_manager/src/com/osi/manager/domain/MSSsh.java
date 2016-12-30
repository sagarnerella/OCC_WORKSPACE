package com.osi.manager.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.osi.manager.vo.Checkpoint;

@XmlRootElement(name = "sshWrapper")
public class MSSsh {
	private String connectType;
	private String hostName;
	private String ipAddress;
	private Integer port;
	private String userName;
	private String password;
	private List<Checkpoint> checkpoint;
	public MSSsh(){}
	
	public List<Checkpoint> getCheckpoint() {
		return checkpoint;
	}
	@XmlElement
	public void setCheckpoint(List<Checkpoint> checkpoint) {
		this.checkpoint = checkpoint;
	}

	public String getConnectType() {
		return connectType;
	}
	@XmlAttribute
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}
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
	public Integer getPort() {
		return port;
	}
	@XmlAttribute
	public void setPort(Integer port) {
		this.port = port;
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
	
}
