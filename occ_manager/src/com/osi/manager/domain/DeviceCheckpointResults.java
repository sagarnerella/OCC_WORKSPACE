package com.osi.manager.domain;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deviceCheckpointResults")
public class DeviceCheckpointResults implements Serializable{
	private List<DeviceCheckpointResult> checkpointResult;

	public List<DeviceCheckpointResult> getCheckpointResult() {
		return checkpointResult;
	}
	
	@XmlElement
	public void setCheckpointResult(List<DeviceCheckpointResult> checkpointResult) {
		this.checkpointResult = checkpointResult;
	}
	
	
}
