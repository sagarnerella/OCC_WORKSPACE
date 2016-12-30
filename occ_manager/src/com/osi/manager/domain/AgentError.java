package com.osi.manager.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AgentError")
public class AgentError implements Serializable{
	private Integer agentId;
	private Long timeOfError;
	private String message;
		
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public Long getTimeOfError() {
		return timeOfError;
	}

	public void setTimeOfError(Long timeOfError) {
		this.timeOfError = timeOfError;
	}

	
	
}
