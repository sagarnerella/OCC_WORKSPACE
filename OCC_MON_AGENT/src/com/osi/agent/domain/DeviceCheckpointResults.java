package com.osi.agent.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deviceCheckpointResults")
public class DeviceCheckpointResults {
	private List<DeviceCheckpointResult> checkpointResult;

	public List<DeviceCheckpointResult> getCheckpointResult() {
		return checkpointResult;
	}

	@XmlElement
	public void setCheckpointResult(List<DeviceCheckpointResult> checkpointResult) {
		this.checkpointResult = checkpointResult;
	}


}
