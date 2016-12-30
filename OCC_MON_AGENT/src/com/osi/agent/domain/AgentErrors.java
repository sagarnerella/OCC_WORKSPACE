/**
 * 
 */
package com.osi.agent.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jkorada1
 *
 */
@XmlRootElement(name = "AgentErrors")
public class AgentErrors {
	private List<AgentError>  agentErrors;

	public List<AgentError> getErrors() {
		return agentErrors;
	}

	@XmlElement
	public void setErrors(List<AgentError> agentErrors) {
		this.agentErrors = agentErrors;
	}


}
