package com.osi.agent.vo;

import java.io.Serializable;

import com.osi.agent.common.CommonUtilities;

public class CheckPointDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer deviceID;
	private String snmpHostName;
	private String snmpIpAddress;
	private Integer snmpPort;
	private String snmpConnectType;
	private String communityString;
	private String snmpVersion;
	private String jmxHostName;
	private String jmxIpAddress;
	private Integer jmxPort;
	private String jmxConnectType;
	private String jdbcConnectType;
	private String driverClass;
	private String dbURL;
	private String userName;
	private String password;
	private Character checkType;
	private String checkPoint;
	private Integer checkpointID;
	private String frequency;
	private Integer repeatCount;
	private String fromDate;
	private String toDate;
	private String startTime;
	private String endTime;
	private String fromDayOfWeek;
	private String toDayOfWeek;
	private String dataType;
	private String startDate;
	private String endDate;
	private String executionType;
	private String supressIncident;
	private String alternateThreshold;
	//For File Monitor
	private String fileLocation;
	//private String identifierFieldSeparator;
	//private String identifierFieldsToConsider;
	private String fileRegExpression;
	private String fileConnectType;
	private String filehHostName;
	private String fileIpAddress;
	private Integer filePort;
	private String fileUserName;
	private String filePassword;
	private String includeParams;
	private String excludeParams;
	//For SSH
	private String sshConnectType;
	private String sshHostName;
	private String sshIpAddress;
	private Integer sshPort;
	private String sshUserName;
	private String sshPassword;
	//For SNMPWALK
	private String snmpWalkAlgorithm;
	
	//For SNMPTABLE
	
	private String snmpTableConnectType;
	private String snmpTableIpAddress;
	private String snmpTableHost;
	private Integer snmpTablePort;
	private String snmpTableOid;
	private String snmpTableAlgorithm;
	private String snmpTableColumnNames;
	private String unitOfMeasure;
	private String snmpTableMibName;
	private int snmpTableRowId;

	public CheckPointDetails(){}
	
	public Integer getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}
	
	public String getSnmpHostName() {
		return snmpHostName;
	}

	public void setSnmpHostName(String snmpHostName) {
		this.snmpHostName = snmpHostName;
	}

	public String getSnmpIpAddress() {
		return snmpIpAddress;
	}

	public void setSnmpIpAddress(String snmpIpAddress) {
		this.snmpIpAddress = snmpIpAddress;
	}

	public Integer getSnmpPort() {
		return snmpPort;
	}

	public void setSnmpPort(Integer snmpPort) {
		this.snmpPort = snmpPort;
	}

	public String getSnmpConnectType() {
		return snmpConnectType;
	}

	public void setSnmpConnectType(String snmpConnectType) {
		this.snmpConnectType = snmpConnectType;
	}

	public String getCommunityString() {
		return communityString;
	}

	public void setCommunityString(String communityString) {
		this.communityString = communityString;
	}

	public String getSnmpVersion() {
		return snmpVersion;
	}

	public void setSnmpVersion(String snmpVersion) {
		this.snmpVersion = snmpVersion;
	}

	public String getJmxHostName() {
		return jmxHostName;
	}

	public void setJmxHostName(String jmxHostName) {
		this.jmxHostName = jmxHostName;
	}

	public String getJmxIpAddress() {
		return jmxIpAddress;
	}

	public void setJmxIpAddress(String jmxIpAddress) {
		this.jmxIpAddress = jmxIpAddress;
	}

	public Integer getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(Integer jmxPort) {
		this.jmxPort = jmxPort;
	}

	public String getJmxConnectType() {
		return jmxConnectType;
	}

	public void setJmxConnectType(String jmxConnectType) {
		this.jmxConnectType = jmxConnectType;
	}

	public String getJdbcConnectType() {
		return jdbcConnectType;
	}

	public void setJdbcConnectType(String jdbcConnectType) {
		this.jdbcConnectType = jdbcConnectType;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Character getCheckType() {
		return checkType;
	}

	public void setCheckType(Character checkType) {
		this.checkType = checkType;
	}

	public String getCheckPoint() {
		return checkPoint;
	}

	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}

	public Integer getCheckpointID() {
		return checkpointID;
	}

	public void setCheckpointID(Integer checkpointID) {
		this.checkpointID = checkpointID;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFromDayOfWeek() {
		return fromDayOfWeek;
	}

	public void setFromDayOfWeek(String fromDayOfWeek) {
		this.fromDayOfWeek = fromDayOfWeek;
	}

	public String getToDayOfWeek() {
		return toDayOfWeek;
	}

	public void setToDayOfWeek(String toDayOfWeek) {
		this.toDayOfWeek = toDayOfWeek;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
	
	public String getSupressIncident() {
		return supressIncident;
	}

	public void setSupressIncident(String supressIncident) {
		this.supressIncident = supressIncident;
	}

	public String getAlternateThreshold() {
		return alternateThreshold;
	}

	public void setAlternateThreshold(String alternateThreshold) {
		this.alternateThreshold = alternateThreshold;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	/*public String getIdentifierFieldSeparator() {
		return identifierFieldSeparator;
	}

	public void setIdentifierFieldSeparator(String identifierFieldSeparator) {
		this.identifierFieldSeparator = identifierFieldSeparator;
	}

	public String getIdentifierFieldsToConsider() {
		return identifierFieldsToConsider;
	}

	public void setIdentifierFieldsToConsider(String identifierFieldsToConsider) {
		this.identifierFieldsToConsider = identifierFieldsToConsider;
	}*/

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFileConnectType() {
		return fileConnectType;
	}

	public void setFileConnectType(String fileConnectType) {
		this.fileConnectType = fileConnectType;
	}
	
	public String getFileRegExpression() {
		return fileRegExpression;
	}

	public void setFileRegExpression(String fileRegExpression) {
		this.fileRegExpression = fileRegExpression;
	}

	public String getSshConnectType() {
		return sshConnectType;
	}

	public void setSshConnectType(String sshConnectType) {
		this.sshConnectType = sshConnectType;
	}

	public String getFilehHostName() {
		return filehHostName;
	}

	public void setFilehHostName(String filehHostName) {
		this.filehHostName = filehHostName;
	}

	public String getFileIpAddress() {
		return fileIpAddress;
	}
	
	public Integer getFilePort() {
		return filePort;
	}

	public void setFilePort(Integer filePort) {
		this.filePort = filePort;
	}

	public void setFileIpAddress(String fileIpAddress) {
		this.fileIpAddress = fileIpAddress;
	}

	public String getFileUserName() {
		return fileUserName;
	}

	public void setFileUserName(String fileUserName) {
		this.fileUserName = fileUserName;
	}

	public String getFilePassword() {
		return filePassword;
	}

	public void setFilePassword(String filePassword) {
		this.filePassword = filePassword;
	}

	public String getSshHostName() {
		return sshHostName;
	}

	public void setSshHostName(String sshHostName) {
		this.sshHostName = sshHostName;
	}

	public String getSshIpAddress() {
		return sshIpAddress;
	}

	public void setSshIpAddress(String sshIpAddress) {
		this.sshIpAddress = sshIpAddress;
	}

	public Integer getSshPort() {
		return sshPort;
	}

	public void setSshPort(Integer sshPort) {
		this.sshPort = sshPort;
	}

	public String getSshUserName() {
		return sshUserName;
	}

	public void setSshUserName(String sshUserName) {
		this.sshUserName = sshUserName;
	}

	public String getSshPassword() {
		return sshPassword;
	}

	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}
	
	public String getIncludeParams() {
		return includeParams;
	}

	public void setIncludeParams(String includeParams) {
		this.includeParams = includeParams;
	}

	public String getExcludeParams() {
		return excludeParams;
	}

	public void setExcludeParams(String excludeParams) {
		this.excludeParams = excludeParams;
	}
	
	public String getSnmpWalkAlgorithm() {
		return snmpWalkAlgorithm;
	}

	public void setSnmpWalkAlgorithm(String snmpWalkAlgorithm) {
		this.snmpWalkAlgorithm = snmpWalkAlgorithm;
	}
	
	// Start: Added for SnmpTable
	
	public int getSnmpTableRowId() {
		return snmpTableRowId;
	}

	public void setSnmpTableRowId(int snmpTableRowId) {
		this.snmpTableRowId = snmpTableRowId;
	}
	public String getSnmpTableMibName() {
		return snmpTableMibName;
	}

	public void setSnmpTableMibName(String snmpTableMibName) {
		this.snmpTableMibName = snmpTableMibName;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getSnmpTableColumnNames() {
		return snmpTableColumnNames;
	}

	public void setSnmpTableColumnNames(String snmpTableColumnNames) {
		this.snmpTableColumnNames = snmpTableColumnNames;
	}

	public String getSnmpTableAlgorithm() {
		return snmpTableAlgorithm;
	}

	public void setSnmpTableAlgorithm(String snmpTableAlgorithm) {
		this.snmpTableAlgorithm = snmpTableAlgorithm;
	}

	public String getSnmpTableOid() {
		return snmpTableOid;
	}

	public void setSnmpTableOid(String snmpTableOid) {
		this.snmpTableOid = snmpTableOid;
	}

	public String getSnmpTableHost() {
		return snmpTableHost;
	}

	public void setSnmpTableHost(String snmpTableHost) {
		this.snmpTableHost = snmpTableHost;
	}

	public String getSnmpTableIpAddress() {
		return snmpTableIpAddress;
	}

	public void setSnmpTableIpAddress(String snmpTableIpAddress) {
		this.snmpTableIpAddress = snmpTableIpAddress;
	}

	public String getSnmpTableConnectType() {
		return snmpTableConnectType;
	}

	public void setSnmpTableConnectType(String snmpTableConnectType) {
		this.snmpTableConnectType = snmpTableConnectType;
	}
	
	public Integer getSnmpTablePort() {
		return snmpTablePort;
	}

	public void setSnmpTablePort(Integer snmpTablePort) {
		this.snmpTablePort = snmpTablePort;
	}

   // End:Added for SnmpTable

	public boolean isValid(CheckPointDetails checkPointDetails) {
		//boolean jmxIpAddressFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getJmxIpAddress());
	//	boolean snmpIpAddressFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getSnmpIpAddress());
		//boolean jmxPortFlag = CommonUtilities.isNotNull(checkPointDetails.getJmxPort());
		//boolean snmpPortFlag = CommonUtilities.isNotNull(checkPointDetails.getSnmpPort());
		boolean checkPointFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getCheckPoint());
		//boolean communityStringFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getCommunityString());
		boolean freequencyFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getFrequency());
		//boolean snmpVersionFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetails.getSnmpVersion());
		if(checkPointFlag && freequencyFlag ){
			return true;
		}else{
			return false;
		}
	}


}
