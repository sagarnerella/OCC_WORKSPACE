package com.osi.re.vo;

import java.sql.Timestamp;

public class MSAgentPollAudit {
	private int pollId;
	private char status;
	private String agentName;
	private String errortype;
	private Timestamp creation_date;
	public MSAgentPollAudit(){}
	public int getPollId() {
		return pollId;
	}
	public void setPollId(int pollId) {
		this.pollId = pollId;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	public String getErrorType() {
		return errortype;
	}
	public void setErrorType(String errortype) {
		this.errortype = errortype;
	}
	public Timestamp getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(Timestamp creation_date) {
		this.creation_date = creation_date;
	}
}

