package com.osi.manager.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.osi.manager.vo.Checkpoint;

@XmlRootElement(name = "snmp")
public class MSSnmp {
	private String communityString;
	private String connectType;
	private String hostName;
	private String ipAddress;
	private Integer port;
	private String snmpVersion;
	private List<Checkpoint> checkpoint;
	public MSSnmp(){}
	
	public List<Checkpoint> getCheckpoint() {
		return checkpoint;
	}
	@XmlElement
	public void setCheckpoint(List<Checkpoint> checkpoint) {
		this.checkpoint = checkpoint;
	}

	public String getCommunityString() {
		return communityString;
	}
	@XmlAttribute
	public void setCommunityString(String communityString) {
		this.communityString = communityString;
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
	public String getSnmpVersion() {
		return snmpVersion;
	}
	@XmlAttribute
	public void setSnmpVersion(String snmpVersion) {
		this.snmpVersion = snmpVersion;
	}
}
