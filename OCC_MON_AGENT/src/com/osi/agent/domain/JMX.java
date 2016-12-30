package com.osi.agent.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "jmx")
public class JMX {
	
	private String hostName;
	private String ipAddress;
	private int port;
	private String connectType;
	private List<Checkpoint> checkpoint;
	
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

	public int getPort() {
		return port;
	}

	@XmlAttribute
	public void setPort(int port) {
		this.port = port;
	}

	public String getConnectType() {
		return connectType;
	}

	@XmlAttribute
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}


	public List<Checkpoint> getCheckpoint() {
		return checkpoint;
	}
	@XmlElement
	public void setCheckpoint(List<Checkpoint> checkpoint) {
		this.checkpoint = checkpoint;
	}

}
