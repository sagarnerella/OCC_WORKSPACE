package com.osi.agent.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "device")
public class Device {
	
	private int deviceID;
	private int deviceStatus;
	private String startTime;
	private String endTime;
	private String fromDayOfWeek;
	private String toDayOfWeek;
	private String startDate;
	private String endDate;
	private SNMP snmp;
	private SNMPWalk snmpWalk;
	private JMX jmx;
	private JDBC jdbc;
	private LogMiner logMiner;
	private SSHWrapper ssh;
	//Added for SnmpTable
	private SnmpTable snmpTable;

	public Device(){

	}

	public int getDeviceID() {
		return deviceID;
	}

	@XmlAttribute
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
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

	public int getDeviceStatus() {
		return deviceStatus;
	}

	@XmlAttribute
	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public SNMP getSnmp() {
		return snmp;
	}
	@XmlElement
	public void setSnmp(SNMP snmp) {
		this.snmp = snmp;
	}
	
	public SNMPWalk getSnmpWalk() {
		return snmpWalk;
	}
	@XmlElement
	public void setSnmpWalk(SNMPWalk snmpWalk) {
		this.snmpWalk = snmpWalk;
	}

	public JMX getJmx() {
		return jmx;
	}
	@XmlElement
	public void setJmx(JMX jmx) {
		this.jmx = jmx;
	}

	public JDBC getJdbc() {
		return jdbc;
	}
	@XmlElement
	public void setJdbc(JDBC jdbc) {
		this.jdbc = jdbc;
	}

	public SSHWrapper getSsh() {
		return ssh;
	}
	@XmlElement
	public void setSsh(SSHWrapper ssh) {
		this.ssh = ssh;
	}

	public LogMiner getLogMiner() {
		return logMiner;
	}
	@XmlElement
	public void setLogMiner(LogMiner logMiner) {
		this.logMiner = logMiner;
	}
	//start for SnmpTable
	public SnmpTable getSnmpTable() {
		return snmpTable;
	}

	@XmlElement
	public void setSnmpTable(SnmpTable snmpTable) {
		this.snmpTable = snmpTable;
	}
	//End for SnmpTable
}
