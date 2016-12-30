/**
 * 
 */
package com.osi.agent.vo;

import java.io.Serializable;
/**
 * @author jkorada1
 *
 */
public class CheckpointResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CheckPointDetails checkPointDetails;
	private String checkPointresult;
	private Long executinTime;
	private int downtimeScheduled;
	private String connectType;
	//Added for SnmpTable
	private String resultDesc;
	private String formatedValue;
	public String getFormatedValue() {
		return formatedValue;
	}
	public void setFormatedValue(String formatedValue) {
		this.formatedValue = formatedValue;
	}
	
	public CheckPointDetails getCheckPointDetails() {
		return checkPointDetails;
	}
	public void setCheckPointDetails(CheckPointDetails checkPointDetails) {
		this.checkPointDetails = checkPointDetails;
	}
	public String getCheckPointresult() {
		return checkPointresult;
	}
	public void setCheckPointresult(String checkPointresult) {
		this.checkPointresult = checkPointresult;
	}
	public Long getExecutinTime() {
		return executinTime;
	}
	public void setExecutinTime(Long executinTime) {
		this.executinTime = executinTime;
	}
	public int getDowntimeScheduled() {
		return downtimeScheduled;
	}
	public void setDowntimeScheduled(int downtimeScheduled) {
		this.downtimeScheduled = downtimeScheduled;
	}
	public String getConnectType() {
		return connectType;
	}
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
}
