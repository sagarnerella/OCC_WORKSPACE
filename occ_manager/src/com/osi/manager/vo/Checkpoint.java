package com.osi.manager.vo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checkpoint")
public class Checkpoint {
	
	private Integer checkpointID;
	private String checkpoint;
	private String frequency;
	private String dataType;
	private String checkType;
	private Integer repeatCount;
	private Integer checkpointStatus;
	private String fromDate;
	private String fileLocation;
	//private String identifierFieldSeparator;
	//private String identifierFieldsToConsider;
	private String includeParams;
	private String excludeParams;
	private String toDate;
	private String executionType;
	private String supressIncident;
	private String alternateThreshold;
	private String indentifierRegexPattern;
	//For SNMPWALK
	private String snmpWalkAlgorithm;
	//Added for SNMPTable
	private String mibName;
	private String snmpTableAlgorithm;
	private String unitOfMeasure;
	private String snmpTableColumnNames;
	private String snmpTableRowId;
	

	public Checkpoint(){}
	
	public String getFileLocation() {
		return fileLocation;
	}
	@XmlAttribute
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	/*public String getIdentifierFieldSeparator() {
		return identifierFieldSeparator;
	}
	@XmlAttribute
	public void setIdentifierFieldSeparator(String identifierFieldSeparator) {
		this.identifierFieldSeparator = identifierFieldSeparator;
	}

	public String getIdentifierFieldsToConsider() {
		return identifierFieldsToConsider;
	}
	@XmlAttribute
	public void setIdentifierFieldsToConsider(String identifierFieldsToConsider) {
		this.identifierFieldsToConsider = identifierFieldsToConsider;
	}*/

	public String getIncludeParams() {
		return includeParams;
	}
	@XmlAttribute
	public void setIncludeParams(String includeParams) {
		this.includeParams = includeParams;
	}

	public String getExcludeParams() {
		return excludeParams;
	}
	@XmlAttribute
	public void setExcludeParams(String excludeParams) {
		this.excludeParams = excludeParams;
	}

	public String getExecutionType() {
		return executionType;
	}
	@XmlAttribute
	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}

	public Integer getCheckpointID() {
		return checkpointID;
	}
	@XmlAttribute
	public void setCheckpointID(Integer checkpointID) {
		this.checkpointID = checkpointID;
	}
	public String getCheckpoint() {
		return checkpoint;
	}
	@XmlAttribute
	public void setCheckpoint(String checkpoint) {
		this.checkpoint = checkpoint;
	}
	public String getFrequency() {
		return frequency;
	}
	@XmlAttribute
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getDataType() {
		return dataType;
	}
	@XmlAttribute
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getCheckType() {
		return checkType;
	}
	@XmlAttribute
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public Integer getRepeatCount() {
		return repeatCount;
	}
	@XmlAttribute
	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}
	public Integer getCheckpointStatus() {
		return checkpointStatus;
	}
	@XmlAttribute
	public void setCheckpointStatus(Integer checkpointStatus) {
		this.checkpointStatus = checkpointStatus;
	}
	public String getFromDate() {
		return fromDate;
	}
	@XmlAttribute
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	@XmlAttribute
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getSupressIncident() {
		return supressIncident;
	}
	@XmlAttribute
	public void setSupressIncident(String supressIncident) {
		this.supressIncident = supressIncident;
	}

	public String getAlternateThreshold() {
		return alternateThreshold;
	}
	@XmlAttribute
	public void setAlternateThreshold(String alternateThreshold) {
		this.alternateThreshold = alternateThreshold;
	}

	public String getIndentifierRegexPattern() {
		return indentifierRegexPattern;
	}
	@XmlAttribute
	public void setIndentifierRegexPattern(String indentifierRegexPattern) {
		this.indentifierRegexPattern = indentifierRegexPattern;
	}

	public String getSnmpWalkAlgorithm() {
		return snmpWalkAlgorithm;
	}
	@XmlAttribute
	public void setSnmpWalkAlgorithm(String snmpWalkAlgorithm) {
		this.snmpWalkAlgorithm = snmpWalkAlgorithm;
	}
	//Start : Added for SNMPTable
	public String getMibName() {
		return mibName;
	}
	@XmlAttribute
	public void setMibName(String mibName) {
		this.mibName = mibName;
	}

	public String getSnmpTableAlgorithm() {
		return snmpTableAlgorithm;
	}
	@XmlAttribute
	public void setSnmpTableAlgorithm(String snmpTableAlgorithm) {
		this.snmpTableAlgorithm = snmpTableAlgorithm;
	}
	
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
	@XmlAttribute
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
	
	public String getSnmpTableColumnNames() {
		return snmpTableColumnNames;
	}
	@XmlAttribute
	public void setSnmpTableColumnNames(String snmpTableColumnNames) {
		this.snmpTableColumnNames = snmpTableColumnNames;
	}
	
	public String getSnmpTableRowId() {
		return snmpTableRowId;
	}
	
	@XmlAttribute
	public void setSnmpTableRowId(String snmpTableRowId) {
		this.snmpTableRowId = snmpTableRowId;
	}
	//End : Added for SNMPTable
}
