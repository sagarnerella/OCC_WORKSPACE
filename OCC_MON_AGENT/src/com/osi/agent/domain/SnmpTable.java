package com.osi.agent.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "snmpTable")
public class SnmpTable {
	private String ipAddress;
	private int port;
	private String connectType;
	private String communityString;
	private String snmpVersion;
	private String hostName;
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
	public String getCommunityString() {
		return communityString;
	}
	@XmlAttribute
	public void setCommunityString(String communityString) {
		this.communityString = communityString;
	}
	public String getSnmpVersion() {
		return snmpVersion;
	}
	@XmlAttribute
	public void setSnmpVersion(String snmpVersion) {
		this.snmpVersion = snmpVersion;
	}
	public List<Checkpoint> getCheckpoint() {
		return checkpoint;
	}
	@XmlElement
	public void setCheckpoint(List<Checkpoint> checkpoint) {
		this.checkpoint = checkpoint;
	}
	
}
