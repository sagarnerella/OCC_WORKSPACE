package com.osi.manager.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AgentPollAudit")
public class AgentPollAudit {
	private String agentId;
	private String status;
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
