package com.osi.manager.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.osi.manager.vo.Checkpoint;

@XmlRootElement(name = "snmp")
public class MSJmx {
	private String connectType;
	private String hostName;
	private String ipAddress;
	private Integer port;
	private List<Checkpoint> checkpoint;
	public MSJmx(){}
	
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
}
