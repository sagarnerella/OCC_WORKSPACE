package com.osi.manager.vo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.osi.manager.domain.FileMonitor;
import com.osi.manager.domain.MSJdbc;
import com.osi.manager.domain.MSJmx;
import com.osi.manager.domain.MSSnmp;
import com.osi.manager.domain.MSSnmpTable;
import com.osi.manager.domain.MSSnmpWalk;
import com.osi.manager.domain.MSSsh;

@XmlRootElement(name = "device")
public class Device {
	private int deviceID;
	/*private String hostName;
	private String ipAddress;
	private int port;
	private String connectType;
	private String communityString;
	private String snmpVersion;*/
	private int deviceStatus;
	//private List<Checkpoint> checkpoint;
	private MSSnmp snmp;
	private MSSnmpWalk snmpWalk;
	private MSJmx jmx;
	private MSJdbc jdbc;
	private MSSsh ssh;
	private FileMonitor logMiner;
	//Added for SNMPTable
	private MSSnmpTable snmpTable;
	
	private String startTime;
	private String endTime;
	private String fromDayOfWeek;
	private String toDayOfWeek;
	private String startDate;
	private String endDate;
	
	private int publisherId;
	
	public Device(){
		
	}

	public MSSsh getSsh() {
		return ssh;
	}

	public void setSsh(MSSsh ssh) {
		this.ssh = ssh;
	}

	public FileMonitor getLogMiner() {
		return logMiner;
	}
	@XmlElement
	public void setLogMiner(FileMonitor logMiner) {
		this.logMiner = logMiner;
	}

	public MSSnmp getSnmp() {
		return snmp;
	}
	@XmlElement
	public void setSnmp(MSSnmp snmp) {
		this.snmp = snmp;
	}
	
	public MSSnmpWalk getSnmpWalk() {
		return snmpWalk;
	}
	@XmlElement
	public void setSnmpWalk(MSSnmpWalk snmpWalk) {
		this.snmpWalk = snmpWalk;
	}

	public MSJmx getJmx() {
		return jmx;
	}
	@XmlElement
	public void setJmx(MSJmx jmx) {
		this.jmx = jmx;
	}

	public MSJdbc getJdbc() {
		return jdbc;
	}
	@XmlElement
	public void setJdbc(MSJdbc jdbc) {
		this.jdbc = jdbc;
	}
	//Start : Added for SNMPTable
	public MSSnmpTable getSnmpTable() {
		return snmpTable;
	}

	@XmlElement
	public void setSnmpTable(MSSnmpTable snmpTable) {
		this.snmpTable = snmpTable;
	}
	//End : Added for SNMPTable
	
	public int getDeviceID() {
		return deviceID;
	}
	
	@XmlAttribute
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	/*public String getHostName() {
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
	}*/

	public int getPublisherId() {
		return publisherId;
	}

	@XmlTransient
	public void setPublisherId(int publisherId) {
		this.publisherId = publisherId;
	}

	public Integer getDeviceStatus() {
		return deviceStatus;
	}
	@XmlAttribute
	public void setDeviceStatus(Integer deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public String getStartTime() {
		return startTime;
	}
	@XmlAttribute
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}
	@XmlAttribute
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFromDayOfWeek() {
		return fromDayOfWeek;
	}
	@XmlAttribute
	public void setFromDayOfWeek(String fromDayOfWeek) {
		this.fromDayOfWeek = fromDayOfWeek;
	}

	public String getToDayOfWeek() {
		return toDayOfWeek;
	}
	@XmlAttribute
	public void setToDayOfWeek(String toDayOfWeek) {
		this.toDayOfWeek = toDayOfWeek;
	}

	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}
	/*@XmlAttribute
	public void setPort(Integer port) {
		this.port = port;
	}*/

	public String getStartDate() {
		return startDate;
	}
	@XmlAttribute
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	@XmlAttribute
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
}
