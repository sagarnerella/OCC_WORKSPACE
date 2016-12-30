package com.osi.manager.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.osi.manager.vo.Checkpoint;

@XmlRootElement(name = "snmp")
public class MSJdbc {
	private String driverClass;
	private String connectType;
	private String dbURL;
	private String userName;
	private String password;
	private Integer port;
	private List<Checkpoint> checkpoint;
	public MSJdbc(){}
	
	public String getDriverClass() {
		return driverClass;
	}
	@XmlAttribute
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDbURL() {
		return dbURL;
	}
	@XmlAttribute
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
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
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
}
