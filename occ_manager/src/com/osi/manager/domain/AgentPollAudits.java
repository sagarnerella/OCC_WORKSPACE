/**
 * 
 */
package com.osi.manager.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jkorada1
 *
 */
@XmlRootElement(name = "AgentPollAudits")
public class AgentPollAudits {
    private List<AgentPollAudit>  agentPollAudits;

	public List<AgentPollAudit> getAgentPollAudits() {
		return agentPollAudits;
	}
	
	@XmlElement
	public void setAgentPollAudits(List<AgentPollAudit> agentPollAudits) {
		this.agentPollAudits = agentPollAudits;
	}   
}
