package com.osi.manager.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.ws.rs.QueryParam;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

import com.osi.manager.config.constants.MSConstants;
import com.osi.manager.scheduler.MSScheduler;
import com.osi.manager.services.CheckTestConnectionInfoWaiter;
import com.osi.manager.services.CheckTestConnectionInfoNotifier;
import com.osi.manager.util.MSConnectionManager;
import com.osi.manager.common.Base64;
import com.osi.manager.common.CommonUtilities;
import com.osi.manager.domain.AgentError;
import com.osi.manager.domain.AgentErrors;
import com.osi.manager.domain.CheckTestConnectionInfo;
import com.osi.manager.domain.DeviceCheckpointResult;
import com.osi.manager.domain.DeviceCheckpointResults;
import com.osi.manager.domain.FileMonitor;
import com.osi.manager.domain.MSJdbc;
import com.osi.manager.domain.MSJmx;
import com.osi.manager.domain.MSSnmp;
import com.osi.manager.domain.MSSnmpTable;
import com.osi.manager.domain.MSSnmpWalk;
import com.osi.manager.domain.MSSsh;
import com.osi.manager.domain.UMObject;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.DBConnectException;
import com.osi.manager.exception.FinderException;
import com.osi.manager.exception.PersistException;
import com.osi.manager.exception.UpdateException;
import com.osi.manager.formator.IFormator;
import com.osi.manager.formator.UMFormatorImpl;
import com.osi.manager.vo.Checkpoint;
import com.osi.manager.vo.Device;
import com.osi.manager.vo.DeviceXml;
import com.osi.manager.vo.MSAgent;
import com.osi.manager.vo.Result;


























import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ManagerDao implements IManagerDao{
	private static final Logger LOGGER = Logger.getLogger(ManagerDao.class);
	public boolean saveMonitoringResult(DeviceCheckpointResults dcpResultList) throws CreateException,FinderException{
		boolean status = false;
		Connection connection=null;
		Statement statement = null;
		List<String> sqlQuery = null;
		String date = null;
		String query2= null;
		
		DeviceCheckpointResult checkPointResultObj=null;
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				if(null!=dcpResultList.getCheckpointResult() && dcpResultList.getCheckpointResult().size() != 0){
					sqlQuery=new ArrayList<String>(0);
					date=getCurrentDate();
					String formattedDate =  date.replaceAll("-", "");
					String tablename = null;
					tablename = "ms_device_checkpoint_result_"+formattedDate;
					DatabaseMetaData meta = connection.getMetaData(); 
					ResultSet res = meta.getTables(null, null, tablename, null); 
					if(!res.next()){ 
						query2 = "create table "+tablename+" like ms_device_checkpoint_result";
						statement=connection.createStatement();
						statement.executeUpdate(query2);
					} 
					String executionTime = null;
					UMObject umObject = null;
					IFormator iFormator = (IFormator)new UMFormatorImpl();
					Map<Integer,UMObject> umFormatMap = getUMFormatMap();
					for(DeviceCheckpointResult checkPointResult : dcpResultList.getCheckpointResult() ){
						checkPointResultObj=checkPointResult;
						String formatedValue = "";
						umObject = umFormatMap.get(checkPointResult.getCheckpointId());
						if (!checkPointResult.getCheckType().equals('E')) {
							if (!checkPointResult.getValue().equalsIgnoreCase("Downtime Scheduled")) {
								if(null != checkPointResult.getConnectType() && (checkPointResult.getConnectType().equalsIgnoreCase(CommonUtilities.getProperty("JDBC")) || 
										checkPointResult.getConnectType().equalsIgnoreCase(CommonUtilities.getProperty("FILE_MONITORING")) || 
										checkPointResult.getConnectType().equalsIgnoreCase(CommonUtilities.getProperty("SSH")))){
									String responseValue = "";
									if (checkPointResult.getValue().indexOf("<RESULT>") == -1) {
										if (checkPointResult.getConnectType().equalsIgnoreCase(CommonUtilities.getProperty("SSH"))) {
											responseValue = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG><ROWSET><ROW><Decription>Invalid Script, please revisit the script that we configured </Decription></ROW></ROWSET></MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
										} else {
											responseValue = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Result not found, please check the configuration parameters</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
										}
									} else {
										responseValue = checkPointResult.getValue();
									}
									LOGGER.info("The resultant response value is :::::::::: "+responseValue);
									Result jResult=(Result)CommonUtilities.getObjectFromXML(responseValue, Result.class);
									
									formatedValue = jResult.getFORMATTEDVALUE();
								}
								else {//Modified for SNMPTABLE
									/*if(!MSConstants.SNMP_TABLE.equalsIgnoreCase(umObject.getConnectType()))
										formatedValue = iFormator.formatValueWithUM(checkPointResult.getValue(), umObject);
									else*/
										formatedValue = checkPointResult.getFormatedValue();
								}
							}
						}if (checkPointResult.getCheckType().equals('E')) {
							if(null!=checkPointResult.getFormatedValue())
							if(checkPointResult.getFormatedValue().equalsIgnoreCase("-1"))
								checkPointResult.setCheckType('R');
								formatedValue = checkPointResult.getFormatedValue();
							
						}
						//executionTime = formatDate(checkPointResult.getCheckpointExecutionTime());
						executionTime =  formatMillisecondsToUTCDate(checkPointResult.getCheckpointExecutionTime());
						//Modified for SNMPTABLE
						if(umObject!=null)
					{/*
						if(!MSConstants.SNMP_TABLE.equalsIgnoreCase(umObject.getConnectType())){
							if(null!=checkPointResult.getValue()){
								Map<String,String> mapData=ManagerDao.getKnownErrors();
								if(mapData!=null)
					            if(mapData.containsKey(checkPointResult.getValue())){
					             //checkPointResult.setValue("-1");
					            	formatedValue=mapData.get(checkPointResult.getValue());
					            }
					             }
						sqlQuery.add("insert into "+tablename+"(value,formated_value,checkpoint_execution_time,checkpoint_id,device_id,check_type,process_status,downtime_scheduled,created_by,creation_date,updated_by,updated_date) " +
								"values('"+checkPointResult.getValue()+"','"+formatedValue+"','"+executionTime+"',"+checkPointResult.getCheckpointId()+","+checkPointResult.getDeviceId()+"," +
								"'"+checkPointResult.getCheckType()+"','"+checkPointResult.getStatus()+"',"+checkPointResult.getDowntimeScheduled()+",1,utc_timestamp(),1,utc_timestamp())");
						}
						else{
						sqlQuery.add("insert into "+tablename+"(value,formated_value,checkpoint_execution_time,checkpoint_id,device_id,check_type,process_status,downtime_scheduled,created_by,creation_date,updated_by,updated_date,resultDesc) " +
									"values('"+checkPointResult.getValue()+"','"+formatedValue+"','"+executionTime+"',"+checkPointResult.getCheckpointId()+","+checkPointResult.getDeviceId()+"," +
									"'"+checkPointResult.getCheckType()+"','"+checkPointResult.getStatus()+"',"+checkPointResult.getDowntimeScheduled()+",1,utc_timestamp(),1,utc_timestamp(),'"+checkPointResult.getResultDesc()+"')");
						}*/
							
							sqlQuery.add("insert into "+tablename+"(value,formated_value,checkpoint_execution_time,checkpoint_id,device_id,check_type,process_status,downtime_scheduled,created_by,creation_date,updated_by,updated_date,resultDesc) " +
									"values('"+checkPointResult.getValue()+"','"+formatedValue+"','"+executionTime+"',"+checkPointResult.getCheckpointId()+","+checkPointResult.getDeviceId()+"," +
									"'"+checkPointResult.getCheckType()+"','"+checkPointResult.getStatus()+"',"+checkPointResult.getDowntimeScheduled()+",1,utc_timestamp(),1,utc_timestamp(),'"+checkPointResult.getResultDesc()+"')");
						
					}	
						checkPointResultObj=null;
					}
					status= persistData(sqlQuery,connection);
					connection.commit();
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (CreateException)handleException(e, "Create", e.getMessage(), "Error occured while saving Monitoring Result for the Device id  "+checkPointResultObj.getDeviceId()+" for the Checkpoint Id "+checkPointResultObj.getCheckpointId(), null);
		} finally{
			if(sqlQuery!=null && !sqlQuery.isEmpty()){
				sqlQuery.clear();
				sqlQuery=null;
			}
			closeDBConnection(connection, statement, null);
		}
		return status;
	}

	public String formatDate(long executionTime){
		Date date=new Date(executionTime);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df2.format(date);
		
	}
	
	public String formatMillisecondsToUTCDate(long executionTime){
		Date date=new Date(executionTime);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateformat.format(date);
		
	}

	public boolean saveAgentErrors(AgentErrors agentErrors) throws CreateException,FinderException{
		boolean status = false;
		Connection connection=null;
		List<String> sqlQuery = null;
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				if(null!=agentErrors.getErrors() && agentErrors.getErrors().size()!=0){
					sqlQuery=new ArrayList<String>(0);
					for(AgentError agentError : agentErrors.getErrors() ){
						String errorOccuredTime = formatDate(agentError.getTimeOfError());
						String agent_error=CommonUtilities.getProperty("AGENT_ERROR");
						sqlQuery.add("insert into ms_agent_errors(agent_id,error_text,created_by,creation_date,updated_by,updated_date,error_creation_time) values("+agentError.getAgentId()+",'"+agentError.getMessage()+"'," +
								"1,'"+Timestamp.valueOf(formatDate(getExecTimeinUTC()))+"',1,'"+Timestamp.valueOf(formatDate(getExecTimeinUTC()))+"','"+errorOccuredTime+"')");
                        
						
						sqlQuery.add("insert into ms_agent_poll_audit(agent_id,status,created_by,creation_date,updated_by,updated_date,error_type) values("+agentError.getAgentId()+",'Z',1,'"+Timestamp.valueOf(formatDate(getExecTimeinUTC()))+"'," +
		                     "1,'"+Timestamp.valueOf(formatDate(getExecTimeinUTC()))+"', '"+agent_error+"')");
					
					}
					status=persistData(sqlQuery,connection);
					connection.commit();
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (CreateException)handleException(e, "Create", e.getMessage(), "Error occured while saving Agent Errors", null);
		} finally{
			if(sqlQuery!=null && !sqlQuery.isEmpty()){
				sqlQuery.clear();
				sqlQuery=null;
			}
			closeDBConnection(connection, null, null);
		}
		return status;
	}
	public boolean saveAgentPollAudit(int agentId,String status) throws CreateException,FinderException{
		boolean flag = false;
		PreparedStatement preparedStatement=null;
		Connection connection=null;	
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				if(agentId!=0 &&  status!=null && !"".equals(status)){
					preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_AUDIT_POLL"));
					preparedStatement.setInt(1,agentId);
					preparedStatement.setString(2,status);
					preparedStatement.setInt(3,1);
					preparedStatement.setTimestamp(4,Timestamp.valueOf(formatDate(getExecTimeinUTC())));
					preparedStatement.setInt(5,1);
					preparedStatement.setTimestamp(6,Timestamp.valueOf(formatDate(getExecTimeinUTC())));
					preparedStatement.execute();
					connection.commit();
					flag = true;
				}	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (CreateException)handleException(e, "Create", "Unable to execute Agent poll audit insert query", "Error occured while saving Agent Poll Audits", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return flag;
	}

	@SuppressWarnings("resource")
	public List<Device> getEntriesToPublish(MSAgent msAgent, String publishType) throws Exception {
		List<Device> deviceList = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		String queryToRetriveAllParams = null;
		String queryToRetriveAllParamsGrp = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				preparedStatement = connection.prepareStatement("SET SESSION group_concat_max_len = "+CommonUtilities.getProperty("MYSQL_GROUP_CONCAT_MAX_LEN")+";");
				preparedStatement.execute();
				String query=CommonUtilities.getProperty("QUERY_TO_RETRIVE_ALL_PARAMS_AS_COLUMNS").replace("@@Checks", "''"+CommonUtilities.getProperty("CHECK_PARAM_TYPE")+"''").replace("@@Asset", "''"+CommonUtilities.getProperty("CONNECTION_PARAM_TYPE")+"''");
				preparedStatement = connection.prepareStatement(query);
				resultSet = preparedStatement.executeQuery();
				if(resultSet.next()){
					queryToRetriveAllParams = resultSet.getString(1); 
					queryToRetriveAllParamsGrp = resultSet.getString(2);
				}else{
					throw (FinderException)handleException(null, "Finder", "Check/Connection parameters are not defined", "Check/Connection parameters are not defined", null);
				}
				if(publishType!=null && publishType.equalsIgnoreCase("ALERT_CONFIG")){
				query=CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_1")
							+" "+queryToRetriveAllParamsGrp
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_2")
							+" "+queryToRetriveAllParams
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_3")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_4")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_5")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_6")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_THRESHOLD_NEW_7");
				}else{
					query=CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_1")
							+" "+queryToRetriveAllParamsGrp
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_2")
							+" "+queryToRetriveAllParams
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_3")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_4")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_5")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_6")
							+" "+CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICE_CHECKPOINT_NEW_7");
				}
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, CommonUtilities.getProperty("CONNECTION_PARAM_TYPE"));
				preparedStatement.setString(2, CommonUtilities.getProperty("CHECK_PARAM_TYPE"));
				preparedStatement.setInt(3, msAgent.getAgentId());
				LOGGER.info("query "+query);
				LOGGER.info("CommonUtilities.getProperty(CONNECTION_PARAM_TYPE) "+CommonUtilities.getProperty("CONNECTION_PARAM_TYPE") );
				LOGGER.info("CommonUtilities.getProperty(CHECK_PARAM_TYPE) "+CommonUtilities.getProperty("CHECK_PARAM_TYPE") );
				resultSet = preparedStatement.executeQuery();
				deviceList = getDeviceList(resultSet,publishType);
				LOGGER.info("deviceList "+deviceList);	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return deviceList;
	}


	private List<Device> getDeviceList(ResultSet resultSet, String publishType) throws FinderException{
		List<Device> deviceList = new ArrayList<Device>(0);
		List<Checkpoint> checkpointList = null;
		Checkpoint checkpoint = null;
		Device device = null;
		String checkType=null;
		Integer checkCount=null;
		Set<Integer> flagSet = null;
		Set<Integer> flagCheckpointSet = null;
		Set<String> flagConTypeSet = null;
		MSSnmp snmp = null;
		MSSnmpWalk snmpWalk = null;
		MSJmx jmx = null;
		MSJdbc jdbc = null;
		MSSsh ssh = null;
		FileMonitor fileMonitor = null;
		//Added for SNMPTable
		MSSnmpTable snmpTable = null;
		try {
			flagSet = new HashSet<Integer>(0); 
			flagCheckpointSet = new HashSet<Integer>(0); 
			flagConTypeSet = new HashSet<String>(0);
			while(resultSet.next()){
				if(flagSet.add(resultSet.getInt("device_id"))){
					device = new Device();
					snmp = new MSSnmp();
					snmpWalk = new MSSnmpWalk();
					jmx = new MSJmx();
					jdbc = new MSJdbc();
					ssh = new MSSsh();
					fileMonitor = new FileMonitor();
					//Added for SNMPTable
					snmpTable = new MSSnmpTable();
					device.setPublisherId(resultSet.getInt("publisher_id"));
					device.setDeviceID(resultSet.getInt("device_id"));
					checkpointList = new ArrayList<Checkpoint>(0);
					/*device.setHostName(resultSet.getString("asset_host_name"));
					device.setIpAddress(resultSet.getString("asset_ip_address"));
					device.setPort(resultSet.getInt("asset_port_number"));
					device.setConnectType(resultSet.getString("at_con_type"));
					device.setCommunityString(resultSet.getString("asset_community_string"));
					device.setSnmpVersion(resultSet.getString("asset_snp_version"));*/
					device.setDeviceStatus(resultSet.getInt("device_status"));
					Device downTimeDetails = getDownTimeScheduleInfo(resultSet.getInt("device_id"));
					if (null != downTimeDetails) {
						device.setStartTime(downTimeDetails.getStartTime());
						device.setEndTime(downTimeDetails.getEndTime());
						device.setFromDayOfWeek(downTimeDetails.getFromDayOfWeek());
						device.setToDayOfWeek(downTimeDetails.getToDayOfWeek());
						device.setStartDate(downTimeDetails.getStartDate());
						device.setEndDate(downTimeDetails.getEndDate());
					}
					deviceList.add(device);
				}
				if(flagConTypeSet.add(resultSet.getString("con_type"))){
					checkpointList = new ArrayList<Checkpoint>(0);
				}
				if(MSConstants.SNMP.equalsIgnoreCase(resultSet.getString("con_type"))){
					snmp.setCommunityString(resultSet.getString(CommonUtilities.getProperty("COMMUNITY_STRING")));
					snmp.setConnectType(resultSet.getString("con_type"));
					snmp.setHostName(resultSet.getString("asset_host_name"));
					snmp.setIpAddress(resultSet.getString("asset_ip_address"));
					snmp.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
					if ("1".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmp.setSnmpVersion("0");
					} else if ("2".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmp.setSnmpVersion("1");
					} else {
						snmp.setSnmpVersion(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")));
					}
					
				} else if(MSConstants.SNMP_WALK.equalsIgnoreCase(resultSet.getString("con_type"))){
					snmpWalk.setCommunityString(resultSet.getString(CommonUtilities.getProperty("COMMUNITY_STRING")));
					snmpWalk.setConnectType(resultSet.getString("con_type"));
					snmpWalk.setHostName(resultSet.getString("asset_host_name"));
					snmpWalk.setIpAddress(resultSet.getString("asset_ip_address"));
					snmpWalk.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
					if ("1".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmpWalk.setSnmpVersion("0");
					} else if ("2".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmpWalk.setSnmpVersion("1");
					} else {
						snmpWalk.setSnmpVersion(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")));
					};
				} else if(MSConstants.JMX.equalsIgnoreCase(resultSet.getString("con_type"))){
					jmx.setConnectType(resultSet.getString("con_type"));
					jmx.setHostName(resultSet.getString("asset_host_name"));
					jmx.setIpAddress(resultSet.getString("asset_ip_address"));
					jmx.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
				}else if(MSConstants.JDBC.equalsIgnoreCase(resultSet.getString("con_type"))){
					jdbc.setConnectType(resultSet.getString("con_type"));
					jdbc.setDriverClass(resultSet.getString(CommonUtilities.getProperty("DRIVER_CLASS")));
					jdbc.setDbURL(resultSet.getString(CommonUtilities.getProperty("DB_URL")));
					jdbc.setUserName(resultSet.getString(CommonUtilities.getProperty("USERNAME")));
					jdbc.setPassword(resultSet.getString(CommonUtilities.getProperty("PASSWORD")));
				}else if(MSConstants.SSH.equalsIgnoreCase(resultSet.getString("con_type"))){
					ssh.setConnectType(resultSet.getString("con_type"));
					ssh.setHostName(resultSet.getString("asset_host_name"));
					ssh.setIpAddress(resultSet.getString("asset_ip_address"));
					ssh.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
					ssh.setUserName(resultSet.getString(CommonUtilities.getProperty("USERNAME")));
					ssh.setPassword(resultSet.getString(CommonUtilities.getProperty("PASSWORD")));
				}else if(MSConstants.FILE_MONITORING.equalsIgnoreCase(resultSet.getString("con_type"))){
					fileMonitor.setConnectType(resultSet.getString("con_type"));
					fileMonitor.setHostName(resultSet.getString("asset_host_name"));
					fileMonitor.setIpAddress(resultSet.getString("asset_ip_address"));
					fileMonitor.setUserName(resultSet.getString(CommonUtilities.getProperty("USERNAME")));
					fileMonitor.setPassword(resultSet.getString(CommonUtilities.getProperty("PASSWORD")));
					fileMonitor.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
				}else if(MSConstants.SNMP_TABLE.equalsIgnoreCase(resultSet.getString("con_type"))){
					//Start: Added else if for SNMPTable
					snmpTable.setCommunityString(resultSet.getString(CommonUtilities.getProperty("COMMUNITY_STRING")));
					snmpTable.setConnectType(resultSet.getString("con_type"));
					snmpTable.setHostName(resultSet.getString("asset_host_name"));
					snmpTable.setIpAddress(resultSet.getString("asset_ip_address"));
					snmpTable.setPort(resultSet.getInt(CommonUtilities.getProperty("PORT")));
					
					if ("1".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmpTable.setSnmpVersion("0");
					} else if ("2".equalsIgnoreCase(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")))) {
						snmpTable.setSnmpVersion("1");
					} else {
						snmpTable.setSnmpVersion(resultSet.getString(CommonUtilities.getProperty("SNMP_VERSION")));
					}
				}
				//End: Added else if for SNMPTable
				
				if (resultSet.getInt("device_status") == 1) {
					if (flagCheckpointSet.add(resultSet.getInt("dev_chk_id"))){
						if (resultSet.getInt("checkpoint_status") == 1){
						checkpoint = new Checkpoint();
						checkpoint.setCheckpointID(resultSet.getInt("dev_chk_id"));
						if(null!=resultSet.getString(CommonUtilities.getProperty("CHECK_POINT")) && !resultSet.getString(CommonUtilities.getProperty("CHECK_POINT")).equals("")){
							checkpoint.setCheckpoint(resultSet.getString(CommonUtilities.getProperty("CHECK_POINT")));
						} else if(null!=resultSet.getString(CommonUtilities.getProperty("QUERY")) && !resultSet.getString(CommonUtilities.getProperty("QUERY")).equals("")){
							checkpoint.setCheckpoint(Base64.encode(resultSet.getString(CommonUtilities.getProperty("QUERY"))));
						} else if(null!=resultSet.getString(CommonUtilities.getProperty("SCRIPT")) && !resultSet.getString(CommonUtilities.getProperty("SCRIPT")).equals("")){
							checkpoint.setCheckpoint(Base64.encode(resultSet.getString(CommonUtilities.getProperty("SCRIPT"))));
						}
						checkpoint.setFrequency(resultSet.getString("schedule_interval"));
						//For JDBC
						if(null!=resultSet.getString(CommonUtilities.getProperty("EXECUTION_TYPE"))){
							if(resultSet.getString(CommonUtilities.getProperty("EXECUTION_TYPE")).equals("1")){
								checkpoint.setExecutionType("plsql");
							}else if(resultSet.getString(CommonUtilities.getProperty("EXECUTION_TYPE")).equals("0")){
								checkpoint.setExecutionType("sql");
							}
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("SUPRESS_INCIDENT"))){
							if(resultSet.getString(CommonUtilities.getProperty("SUPRESS_INCIDENT")).equals("1")){
								checkpoint.setSupressIncident("Yes");
							}else if(resultSet.getString(CommonUtilities.getProperty("SUPRESS_INCIDENT")).equals("0")){
								checkpoint.setSupressIncident("No");
							}
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("ALTERNATE_THRESHOLD"))){
							checkpoint.setAlternateThreshold(resultSet.getString(CommonUtilities.getProperty("ALTERNATE_THRESHOLD")));
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("SNMP_WALK_ALGORITHM"))){
							checkpoint.setSnmpWalkAlgorithm(resultSet.getString(CommonUtilities.getProperty("SNMP_WALK_ALGORITHM")));
						}
						/*Log Miner*/
						if(null!=resultSet.getString(CommonUtilities.getProperty("ABS_FILE_PATH"))){
							checkpoint.setFileLocation(resultSet.getString(CommonUtilities.getProperty("ABS_FILE_PATH")));
						}
						/*if(null!=resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_FIELD_SEPARATOR"))){
						checkpoint.setIdentifierFieldSeparator(resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_FIELD_SEPARATOR")));
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_FIELDS_TO_CONSIDER"))){
						checkpoint.setIdentifierFieldsToConsider(resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_FIELDS_TO_CONSIDER")));
						}*/
						
						if(null!=resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_REGEX_PATTERN"))){
							checkpoint.setIndentifierRegexPattern(resultSet.getString(CommonUtilities.getProperty("IDENTIFIER_REGEX_PATTERN")));
						}
						
						if(null!=resultSet.getString(CommonUtilities.getProperty("INCLUDE_PARAMS"))){
						checkpoint.setIncludeParams(resultSet.getString(CommonUtilities.getProperty("INCLUDE_PARAMS")));
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("EXCLUDE_PARAMS"))){
						checkpoint.setExcludeParams(resultSet.getString(CommonUtilities.getProperty("EXCLUDE_PARAMS")));
						}
						if (publishType!=null && !publishType.equalsIgnoreCase("ALERT_CONFIG")) {
							if (null != resultSet.getDate("from_date")) {
								checkpoint.setFromDate(resultSet.getDate("from_date").toString());
							} 
							if (null != resultSet.getDate("to_date")) {
								checkpoint.setToDate(resultSet.getDate("to_date").toString());
								}
						}
						
						//checkpoint.setCheckpointStatus(resultSet.getInt("checkpoint_status"));
						if(publishType!=null && publishType.equalsIgnoreCase("ALERT_CONFIG")){
							checkType="T";
							checkCount=resultSet.getInt("repeat_count");
						}else{
							checkType="R";
							checkCount=0;
						}
						checkpoint.setCheckType(checkType);
						checkpoint.setRepeatCount(checkCount);
						//Start : Added for SNMPTable
						if(null!=resultSet.getString(CommonUtilities.getProperty("MIB_NAME"))){
							checkpoint.setMibName(resultSet.getString(CommonUtilities.getProperty("MIB_NAME")));
						}
						
						if(null!=resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_ALGORITHM"))){
							checkpoint.setSnmpTableAlgorithm(resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_ALGORITHM")));
						}
						
						if(null!=resultSet.getString(CommonUtilities.getProperty("CACM_SUB_PARAM_ORDER"))){
							checkpoint.setSnmpTableRowId(resultSet.getString(CommonUtilities.getProperty("CACM_SUB_PARAM_ORDER")));
						}
						
						if(MSConstants.SNMP_TABLE.equalsIgnoreCase(resultSet.getString("con_type"))){
						if(null!=resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_UNIT_OF_MEASURE"))){
							checkpoint.setUnitOfMeasure(resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_UNIT_OF_MEASURE")));
						}
						if(null!=resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_COLUMN_NAMES"))){
							checkpoint.setSnmpTableColumnNames(resultSet.getString(CommonUtilities.getProperty("SNMP_TABLE_COLUMN_NAMES")));
						}
						}
						//End : Added for SNMPTable
						checkpointList.add(checkpoint);
							if(MSConstants.SNMP.equalsIgnoreCase(resultSet.getString("con_type"))){
								snmp.setCheckpoint(checkpointList);
								device.setSnmp(snmp);
							} else if(MSConstants.SNMP_WALK.equalsIgnoreCase(resultSet.getString("con_type"))){
								snmpWalk.setCheckpoint(checkpointList);
								device.setSnmpWalk(snmpWalk);
							} else if(MSConstants.JMX.equalsIgnoreCase(resultSet.getString("con_type"))){
								jmx.setCheckpoint(checkpointList);
								device.setJmx(jmx);
							} else if(MSConstants.JDBC.equalsIgnoreCase(resultSet.getString("con_type"))){
								jdbc.setCheckpoint(checkpointList);
								device.setJdbc(jdbc);
							} else if(MSConstants.SSH.equalsIgnoreCase(resultSet.getString("con_type"))){
								ssh.setCheckpoint(checkpointList);
								device.setSsh(ssh);
							} else if(MSConstants.FILE_MONITORING.equalsIgnoreCase(resultSet.getString("con_type"))){
								fileMonitor.setCheckpoint(checkpointList);
								device.setLogMiner(fileMonitor);
							}else if(MSConstants.SNMP_TABLE.equalsIgnoreCase(resultSet.getString("con_type"))){
								//Added else if for SNMPTable
								snmpTable.setCheckpoint(checkpointList);
								device.setSnmpTable(snmpTable);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			if(flagSet!=null && !flagSet.isEmpty()){
				flagSet.clear();
				flagSet=null;
			}
			if(flagCheckpointSet!=null && !flagCheckpointSet.isEmpty()){
				flagCheckpointSet.clear();
				flagCheckpointSet=null;
			}
		}
		return deviceList;
	}
	
	public List<Device> getDevicesToUnschedule(MSAgent msAgent) throws Exception {
		List<Device> deviceList = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				String query=CommonUtilities.getProperty("QUERY_RETRIEVE_DEVICES_UNSCHEDULE");
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, msAgent.getAgentId());
				resultSet = preparedStatement.executeQuery();
				deviceList = getDeviceUnscheduleList(resultSet);	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return deviceList;
	}
	
	private List<Device> getDeviceUnscheduleList(ResultSet resultSet) throws FinderException{
		List<Device> deviceList = new ArrayList<Device>(0);
		Device device = null;
		Set<Integer> flagSet = null;
		try {
			flagSet = new HashSet<Integer>(0); 
			while(resultSet.next()){
				if(flagSet.add(resultSet.getInt("device_id"))){
					device = new Device();
					device.setPublisherId(resultSet.getInt("publisher_id"));
					device.setDeviceID(resultSet.getInt("device_id"));
					deviceList.add(device);
				} else {
					updatePublisherStatus(resultSet.getInt("publisher_id"),"P","","");
				}
			}
		} catch (SQLException e) {
			LOGGER.error("",e);
			throw new FinderException(MSConstants.ERROR_CODE_7002, "Error occured while parsing the result", e.getMessage());
		} catch (UpdateException e) {
			throw new FinderException(MSConstants.ERROR_CODE_7002, "Error occured while updating the publisher", e.getMessage());
		}finally{
			if(flagSet!=null && !flagSet.isEmpty()){
				flagSet.clear();
				flagSet=null;
			}
		}
		return deviceList;
	}
	
	private Device getDownTimeScheduleInfo(int deviceId) throws FinderException {
		Device device = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				statement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_RETRIEVE_DOWNTIME_SCHEDULE_INFO"));
				statement.setInt(1, deviceId);
				resultSet = statement.executeQuery();
				while(resultSet.next()){
					device = new Device();
					device.setStartTime(resultSet.getString("start_time"));
					device.setEndTime(resultSet.getString("end_time"));
					device.setFromDayOfWeek(resultSet.getString("from_day_of_week"));
					device.setToDayOfWeek(resultSet.getString("to_day_of_week"));
					device.setStartDate(resultSet.getString("start_date"));
					device.setEndDate(resultSet.getString("end_date"));
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, statement, null);
		}
		return device;
	}

	public List<MSAgent> getAgentsList() throws FinderException {
		List<MSAgent> agentsList = null;
		MSAgent msAgent = null;
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				statement = connection.createStatement();
				resultSet = statement.executeQuery(CommonUtilities.getProperty("QUERY_RETRIEVE_AGENT_LIST_NEW"));
				agentsList = new ArrayList<MSAgent>(0);
				while(resultSet.next()){
					msAgent = new MSAgent();
					msAgent.setAgentId(resultSet.getInt("agent_id"));
					msAgent.setAgentName(resultSet.getString("agent_name"));
					msAgent.setDeviceId(resultSet.getInt("device_id"));
					msAgent.setIpAddress(resultSet.getString("asset_ip_address"));
					msAgent.setHostName(resultSet.getString("asset_host_name"));
					msAgent.setPort(resultSet.getInt("agent_port"));
					agentsList.add(msAgent);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, statement, null);
		}
		return agentsList;
	} 

	public boolean updatePublisherStatus(int publisherId,String status,String xmlFormat,String error) throws FinderException,UpdateException{
		boolean updateStatus = false;
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_PUBLISHER"));
				preparedStatement.setString(1, status);
				preparedStatement.setString(2, xmlFormat);
				preparedStatement.setString(3, error);
				preparedStatement.setTimestamp(4, Timestamp.valueOf(formatDate(getExecTimeinUTC())));
				preparedStatement.setInt(5, publisherId);
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (UpdateException)handleException(e, "Update", "Unable to execute Publisher Details query", "Error occured while updating Publisher Details", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return updateStatus;
	}
	
	public Map<Integer,UMObject> getUMFormatMap() throws Exception {
		Map<Integer,UMObject> UMFormatMap = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				String query=null;
				query=CommonUtilities.getProperty("QUERY_RETRIEVE_UM_CHECKPOINT_1")
					 +" "+CommonUtilities.getProperty("QUERY_RETRIEVE_UM_CHECKPOINT_2")
					 +" "+CommonUtilities.getProperty("QUERY_RETRIEVE_UM_CHECKPOINT_3")
					 +" "+CommonUtilities.getProperty("QUERY_RETRIEVE_UM_CHECKPOINT_4");
				preparedStatement = connection.prepareStatement(query);
				resultSet = preparedStatement.executeQuery();
				UMFormatMap = new HashMap<Integer,UMObject>();
				UMObject umObject = null;
				while(resultSet.next()){
					umObject = new UMObject();
					/*umObject.setUnitOfMeasure(resultSet.getString("scat_unit_of_measure"));
					umObject.setConnectType(resultSet.getString("at_con_type"));
					UMFormatMap.put(resultSet.getInt("device_checkpoint_id"), umObject);*/
					umObject.setUnitOfMeasure(resultSet.getString("chk_unit_of_measure"));
					umObject.setConnectType(resultSet.getString("ct_name"));
					UMFormatMap.put(resultSet.getInt("dev_chk_id"), umObject);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return UMFormatMap;
	}
	
	private String getCurrentDate() throws FinderException{
		Connection  connection=null;
		Statement statement=null;
		String currentDate=null;
		try{
			String query1=CommonUtilities.getProperty("QUERY_RETRIEVE_CURRENT_DATE");
			connection= MSConnectionManager.getConnection();
			if(connection!=null){
				statement = connection.createStatement();
				ResultSet resultset = statement.executeQuery(query1);
				if (resultset.next()) {
					currentDate = resultset.getString("UTC_DATE()");
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, statement, null);
		}
		return currentDate;
	}
	
	private boolean persistData(List<String> sqlQuery, Connection  connection) throws PersistException{
		boolean persistStatus=false;
		try {
			Statement statement = connection.createStatement();
			for (String sql : sqlQuery) {
				statement.addBatch(sql);
			}
			statement.executeBatch();
			persistStatus=true;
		} catch (Exception e) {
			throw (PersistException)handleException(e, "Persist", "", "Unable to execute batch", null);
		}
		return persistStatus;
	}
	
	private Exception handleException(Exception e, String exceptionType, String sqlExceptionMessage, String globalExceptionMessage, Connection connection){
		Exception exception=null;
		if("Finder".equalsIgnoreCase(exceptionType)){
			if(e instanceof DBConnectException){
				DBConnectException dbException=(DBConnectException)e;
				exception=new FinderException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
			}else if(e instanceof SQLException){
				exception=new FinderException(MSConstants.ERROR_CODE_7002, sqlExceptionMessage, e);
			}else if(e instanceof FinderException){
				FinderException fException=(FinderException)e;
				exception=new FinderException(fException.getCode(), fException.getUserMessage(), e);
			}else{
				exception=new FinderException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
			}
		}else if("Create".equalsIgnoreCase(exceptionType)){
			if(e instanceof DBConnectException){
				DBConnectException dbException=(DBConnectException)e;
				exception=new CreateException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
			}else if(e instanceof PersistException){
				PersistException pException=(PersistException)e;
				exception=new CreateException(pException.getCode(), pException.getUserMessage(), e);
				try{
					connection.rollback();
				}catch (Exception e1) {
					exception=new CreateException(MSConstants.ERROR_CODE_7002, "Unable to rollback", e1.getMessage()+"::"+e.getMessage());
				}
			}else{
				exception=new CreateException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
			}
		}else if("Update".equalsIgnoreCase(exceptionType)){
			if(e instanceof DBConnectException){
				DBConnectException dbException=(DBConnectException)e;
				exception=new UpdateException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
			}else if(e instanceof PersistException){
				PersistException pException=(PersistException)e;
				exception=new UpdateException(pException.getCode(), pException.getUserMessage(), e);
				try{
					connection.rollback();
				}catch (Exception e1) {
					exception=new UpdateException(MSConstants.ERROR_CODE_7002, "Unable to rollback", e1.getMessage()+"::"+e.getMessage());
				}
			}else{
				exception=new UpdateException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
			}
		}else if("Persist".equalsIgnoreCase(exceptionType)){
			exception=new PersistException(MSConstants.ERROR_CODE_7002, globalExceptionMessage, e.getMessage());
		}
		return exception;
	}
	
	private void closeDBConnection(Connection connection, Statement statement, ResultSet resultSet){
		if(resultSet!=null){
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.error("",e);
			}
		}
		if(statement!=null){
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.error("",e);
			}
		}
		if(connection!=null){
			try {
				connection.commit();
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("",e);
			}
			connection=null;
		}
	}
	
	
	public boolean saveDeviceXml(int agentId,String xml,String jobName) throws CreateException,FinderException{
		boolean flag = false;
		PreparedStatement preparedStatement=null;
		Connection connection=null;	
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				if(agentId!=0 &&  xml!=null && !"".equals(xml)){
					preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_XML_FILE"));
					preparedStatement.setInt(1,agentId);
					preparedStatement.setString(2,xml);
					preparedStatement.setString(3,"N");
					preparedStatement.setString(4,jobName);
					preparedStatement.execute();
					connection.commit();
					flag = true;
				}	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (CreateException)handleException(e, "Create", "Unable to execute saveDeviceXml insert query", "Error occured while saving saveDeviceXml ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: saveDeviceXml : return saveDeviceXml flag  status ... "+flag);
		return flag;
	}
	
	public List<DeviceXml> getDeviceXmlList(String jobname,int agentId) throws FinderException{
		List<DeviceXml> deviceXmlList = null;
		DeviceXml deviceXml = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement=null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_FETCH_XML_FILE"));
				preparedStatement.setInt(1,agentId);
				preparedStatement.setString(2,jobname);
				resultSet = preparedStatement.executeQuery();
				deviceXmlList = new ArrayList<DeviceXml>(0);
				while(resultSet.next()){
					deviceXml = new DeviceXml();
					deviceXml.setId(resultSet.getInt("id"));
					deviceXml.setAgentId(resultSet.getInt("agent_id"));
					deviceXml.setJobName(resultSet.getString("job_name"));
					deviceXml.setStatus(resultSet.getString("status"));
					deviceXml.setDeviceXml(resultSet.getString("device_xml"));
					deviceXmlList.add(deviceXml);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the getDeviceXmlList query", "Error occured while parsing the result", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: deviceXmlList : return deviceXmlList... "+deviceXmlList);
		return deviceXmlList;
	}
	
	public boolean updateSaveDeviceXml(int id,int agentId,String jobName,String status) throws FinderException,UpdateException{
		boolean updateStatus = false;
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_XML_FILE"));
				preparedStatement.setString(1, status);
				preparedStatement.setInt(2, id);
				preparedStatement.setInt(3, agentId);
				preparedStatement.setString(4, jobName);
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;	
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (UpdateException)handleException(e, "Update", "Unable to update updateSaveDeviceXml query", "Error occured while updating updateSaveDeviceXml Details", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		return updateStatus;
	}
	
	
	public boolean saveAgentHeartBeat(String agentId) throws CreateException,FinderException{
		boolean flag = false;
		PreparedStatement preparedStatement=null;
		Connection connection=null;	
		try{
			LOGGER.info("ManagerDao :: saveAgentHeartBeat : start ... ");
			int count=0;
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				
					preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_AGENT_HEARTBEAT"));
					preparedStatement.setString(1,agentId);
					ResultSet rs = preparedStatement.executeQuery();
					if (rs.next()) {
				         count = rs.getInt(1);
				       } 
						
				         LOGGER.info("No of records in agent heart beat table with : "+agentId+" is : "+count);
					if(count==0){
						preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_AGENT_HEARTBEAT"));
						preparedStatement.setString(1,agentId);
						//preparedStatement.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
						
						preparedStatement.setTimestamp(2,Timestamp.valueOf(formatDate(getExecTimeinUTC())));
						preparedStatement.execute();
						connection.commit();
						flag = true;
					}else{
						preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_AGENT_HEARTBEAT"));
						preparedStatement.setTimestamp(1,Timestamp.valueOf(formatDate(getExecTimeinUTC())));
						preparedStatement.setString(2,agentId);
						preparedStatement.execute();
						connection.commit();
						flag = true;
					}
					
			}else{
				LOGGER.info("ManagerDao :: saveAgentHeartBeat : Unable to establish db connection ");
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			LOGGER.error("ManagerDao :: saveAgentHeartBeat : Unable to saveAgentHeartBeat insertorupdate query ",e);
			throw (CreateException)handleException(e, "Create", "Unable to execute saveAgentHeartBeat insertorupdate query", "Error occured while saving saveAgentHeartBeat ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: saveAgentHeartBeat : return status flag ... "+flag);
		return flag;
	}
	
	/** crontrigger  to check agent is alive or not  */
	public boolean agentAvailabilityStatus() throws CreateException,FinderException{
		boolean flag = false;
		int  agentId=0;
		try{
			LOGGER.info("ManagerDao :: agentAvailabilityStatus : start ... ");
			
			 List<MSAgent> msAgentsList = getAgentsList();
					for (MSAgent msAgent : msAgentsList) {
							LOGGER.info(" ManagerStartupProcessor :: ScheduleAgentAvailabilityStatus for Agent : start : " +msAgent.getAgentId());
							     agentId=msAgent.getAgentId();
						
						             JobDetail jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_JOB_BASE_NAME")+agentId, CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_JOB_GROUP_BASE_NAME")+agentId, com.osi.manager.jobs.AgentAvailabilityScheduleJob.class);
								     jobDetail.getJobDataMap().put(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_AGENT_ID"), agentId);
								     SimpleTrigger simpleTrigger = MSScheduler.createSimpleTriggerWRCount(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_BASE_NAME")+agentId, CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_GROUP_BASE_NAME")+agentId,3, Integer.parseInt(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_FREQUENCY")));
									 jobDetail.getJobDataMap().put(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_BASE_NAME"), CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_GROUP_BASE_NAME")+agentId);
									 MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
								    
					}//for
					
						flag=true;
			
		} catch (Exception e) {
			LOGGER.error("ManagerDao :: agentAvailabilityStatus : got error ... ");
			e.printStackTrace();
			
		} 
		LOGGER.info("ManagerDao :: agentAvailabilityStatus : end ... "+flag);
		return flag;
	}
	
	
	/** send the devicexmlfileslist  to the agent 	  */
	public String getDeviceXmlList(String agentId) throws CreateException,FinderException{
		StringBuffer sbListofFileNames=new StringBuffer();
		   String detectedOS=null;
		   String getDeviceXmlSPath=null;
		   String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		   LOGGER.info("ManagerDao :: getDeviceXmlList : start ... ");
		   LOGGER.info("ManagerDao :: getDeviceXmlList : present OS environment : "+OS);
		    if (OS.indexOf("win") >= 0) {
		                detectedOS ="windows";
		              } else if (OS.indexOf("nux") >= 0) {
		                detectedOS ="linux";
		              } else {
		                detectedOS ="other";
		        }
		    if(detectedOS.equals("windows")){
		    	getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
		    	getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+agentId;
			}else if(detectedOS.equals("linux")){
				getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_LINUX");
				getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_LINUX")+"_"+agentId;
			}else{
				getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
		    	getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+agentId;
			}
		
		
		File directory = new File(getDeviceXmlSPath);
	        //get all the files from a directory
	        File[] fList = directory.listFiles();
	      
	        if(fList!=null){
				        int noofxmlfiles=fList.length;
				        int increment=0;
				        LOGGER.info("nooffiles ::: "+noofxmlfiles);
				       if( noofxmlfiles>0){
				        for (File file : fList){
				            if (file.isFile()){
				                LOGGER.info(file.getName());
				                sbListofFileNames=sbListofFileNames.append(file.getName());
				                if(increment<noofxmlfiles-1){
				                	sbListofFileNames=sbListofFileNames.append("|");
				                    increment++;
				                 // LOGGER.info("increment ::: "+increment);
				                }
				            }
				        }
				        LOGGER.info("ManagerDao :: getDeviceXmlList : sbListofFileNames ..... :  "+sbListofFileNames);
				        return sbListofFileNames.toString();
				       }else{
				        	LOGGER.info("ManagerDao :: getDeviceXmlList : No files found for requested agentid : "+ agentId);
				             return "No files found for requested agentId";
				        }
	        }else{
	        	LOGGER.info("ManagerDao :: getDeviceXmlList : No files found for requested agentid : "+ agentId);
	             return "No files found for requested agentId";
	        }
	}
	
	/** send the devicexml content  to the agent   */
	public String getDeviceXml(String agentId, String deviceXmlName) throws CreateException,FinderException{
		StringWriter stw;
		String getDeviceXmlContent = null;
		   String detectedOS=null;
		   String getDeviceXmlSPath=null;
		   String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		   LOGGER.info("ManagerDao :: getDeviceXml : start ... ");
		   LOGGER.info("ManagerDao :: getDeviceXml : present OS environment : "+OS);
		    if (OS.indexOf("win") >= 0) {
		                detectedOS ="windows";
		              } else if (OS.indexOf("nux") >= 0) {
		                detectedOS ="linux";
		              } else {
		                detectedOS ="other";
		        }
		    if(detectedOS.equals("windows")){
		    	getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
		    	getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+agentId+"\\"+deviceXmlName;
			}else if(detectedOS.equals("linux")){
				getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_LINUX");
				getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_LINUX")+"_"+agentId+"/"+deviceXmlName;
			}else{
				getDeviceXmlSPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
		    	getDeviceXmlSPath=getDeviceXmlSPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+agentId+"\\"+deviceXmlName;
			}
		    
		    LOGGER.info("ManagerDao :: getDeviceXml file path ::: "+getDeviceXmlSPath);
		    try{ 
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); 
	            InputStream inputStream = new FileInputStream(new File(getDeviceXmlSPath));
	            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream); 
	             stw = new StringWriter(); 
	            Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
	            serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
	            getDeviceXmlContent=stw.toString(); 
	          }catch(FileNotFoundException e){
	        	  e.printStackTrace(); 
	        	  getDeviceXmlContent="Requested File is Not Found";
	          }
	          catch (Exception e) { 
	            e.printStackTrace(); 
	            getDeviceXmlContent="Unable to fetch the content for the xml";
	          } 
		    LOGGER.info("ManagerDao :: getDeviceXml : "+deviceXmlName+" content : "+getDeviceXmlContent);
		 return getDeviceXmlContent;
	}
	
	
	private long getExecTimeinUTC() {
	     Date utcDate = null;
	  try {
	   SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	   SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   utcDate = dateformat.parse(utcFormat.format(System.currentTimeMillis()));
	  } catch (ParseException e) {
	   LOGGER.error("",e);
	  }
	    return utcDate.getTime();
	 }
	

	
	/** send the getCheckinfo to the agent   */
	public String getCheckinfo(String agentId, String address, String tableOid, String mib, String communityStrg) throws CreateException,FinderException{
		String checkstatus = null;
		PreparedStatement preparedStatement=null;
		Connection connection=null;	
		
		String checkresult=null;
		int count=0;
		long lStartTime = new Date().getTime(); // start time
		LOGGER.info("ManagerDao :: getCheckinfo : Start time in milliseconds: "+lStartTime);
		
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
		
			preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_TEST_CON_CHECK_DETAILS"));
			preparedStatement.setString(1,agentId);
			
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				agentId = rs.getString(1);
				count=1;
			}
			LOGGER.info("ManagerDao :: getCheckinfo : inserting check details into the db ..... ");
			/* start insert checkdetails in the table*/
			 if(count==0){
		        preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_TEST_CON_CHECK_DETAILS"));
				preparedStatement.setString(1,agentId);
				preparedStatement.setString(2,address);
				preparedStatement.setString(3,tableOid);
				preparedStatement.setString(4,mib);
				preparedStatement.setString(5,communityStrg);
				preparedStatement.setString(6,"N");
				preparedStatement.setString(7,"request processing");
				preparedStatement.executeUpdate();
				connection.commit();
			 }else{
				preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_TEST_CON_CHECK_DETAILS"));
				preparedStatement.setString(1,"N");
				preparedStatement.setString(2,"request processing");
				preparedStatement.setString(3,agentId);
				preparedStatement.executeUpdate();
				connection.commit();
			 }
				/* end insert checkdetails in the table*/
		
			
			// start wait and notify logic 
			CheckTestConnectionInfo checktestconinfo=new CheckTestConnectionInfo(agentId);
			CheckTestConnectionInfoWaiter waiter = new CheckTestConnectionInfoWaiter(checktestconinfo);
			Thread t1= new Thread(waiter,"waiter");
			t1.start();
	       
	        CheckTestConnectionInfoNotifier notifier = new CheckTestConnectionInfoNotifier(checktestconinfo);
	        Thread t2= new Thread(notifier, "notifier");
	        t2.start();
	        LOGGER.info("ManagerDao :: getCheckinfo : Waiter and notifier threads are started .......... ");
	        
	        t2.join();
	        t1.join();
	        
	     
	        checkresult=checktestconinfo.getCheckResult();
	        checkstatus=checktestconinfo.getCheckStatus();
	       LOGGER.info("ManagerDao :: getCheckinfo : getcheckresult :  "+checktestconinfo.getCheckResult());
			
		/*	end wait and notify logic */
	        if(checkstatus!=null && "P".equals(checkstatus)){
				preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_DELETE_TEST_CON_CHECK_DETAILS"));
			    preparedStatement.setString(1,agentId);
			    preparedStatement.executeUpdate();
			    connection.commit();
			    LOGGER.info("ManagerDao :: getCheckinfo : deleted checkdetails with agentid : "+agentId+" from ms_check_details table");
			}
	    
	  }else{
			checkresult="Unable to establish db connection";
			throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
		}
	} catch (InterruptedException e) {
		LOGGER.error("ManagerDao :: getCheckinfo : ");
		 e.printStackTrace();
    }catch (Exception e) {
    	checkresult="Unable to execute getCheckinfo";
					throw (CreateException)handleException(e, "Create", "Unable to execute getCheckinfo insert query", "Error occured while execute getCheckStatus ", null);
		} finally{
					closeDBConnection(connection, preparedStatement, null);
		}
		
		long lEndTime = new Date().getTime(); // end time
		LOGGER.info("ManagerDao :: getCheckinfo : end time in milliseconds: "+lEndTime);
		
		long difference = lEndTime - lStartTime; // check different
		LOGGER.info("Elapsed milliseconds: " + difference);
		
		double time_in_seconds = difference / 1000.0;   // add the decimal
		LOGGER.info("ManagerDao :: getCheckinfo : Time taken to repond for test connection check info : time in seconds :: "+time_in_seconds); 
		
		LOGGER.info("ManagerDao :: getCheckinfo : return checktestconnectioninfo to ui .................. "+checkresult);
		
		return checkresult;
		
 }
	
	/** send the sendTestConnectionInfo to the agent   */
	public String sendTestConnectionInfo(String agentId) throws CreateException,FinderException{
		PreparedStatement preparedStatement=null;
		Connection connection=null;
		String address = null;
		String tableoid = null;
		String mib = null;
		String communitystrg = null;
		String resultUrl= null;
		try{
			LOGGER.info("ManagerDao :: sendTestConnectionInfo : start ... ");
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				    preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_TEST_CON_CHECK_DETAILS"));
				    preparedStatement.setString(1,agentId);
				    ResultSet rs = preparedStatement.executeQuery();
					if (rs.next()) {
						agentId=rs.getString(1);
						 address = rs.getString(2);
						 tableoid = rs.getString(3);
						 mib = rs.getString(4);
						 communitystrg = rs.getString(5);
						} 
					
					resultUrl="address="+address+"&tableoid="+tableoid+"&mib="+mib+"&communitystrg="+communitystrg;
					
			}else{
				resultUrl="";
				LOGGER.error("ManagerDao :: sendTestConnectionInfo : got error ... "+resultUrl);
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			resultUrl="";
			LOGGER.error("ManagerDao :: sendTestConnectionInfo : got error  ... "+resultUrl);
			throw (CreateException)handleException(e, "Create", "Unable to execute sendTestConnectionStatus select query", "Error occured while executing sendTestConnectionStatus select query ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: sendTestConnectionInfo : return resultUrl ... "+resultUrl);
		return resultUrl;
	}
	
	/** send the getTestConnectionStatus to the agent   */
	public String getTestConnectionInfoResult(String agentId, String checkresult) throws CreateException,FinderException{
		String resultstatus = "";
		PreparedStatement preparedStatement=null;
		Connection connection=null;	
		
		try{
			LOGGER.info("ManagerDao :: getTestConnectionInfoResult : start ... ");
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				    preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_TEST_CON_CHECK_DETAILS"));
					preparedStatement.setString(1,"P");
					preparedStatement.setString(2,checkresult);
					preparedStatement.setString(3,agentId);
					
					preparedStatement.executeUpdate();
					connection.commit();
					
					resultstatus="success";
			}else{
				resultstatus="failure";
				LOGGER.error("ManagerDao :: getTestConnectionInfoResult : return resultstatus ... "+resultstatus);
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			resultstatus="failure";
			LOGGER.error("ManagerDao :: getTestConnectionInfoResult : return resultstatus ... "+resultstatus);
			throw (CreateException)handleException(e, "Create", "Unable to execute getCheckStatus insert query", "Error occured while execute getCheckStatus ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: getTestConnectionInfoResult : return resultstatus ... "+resultstatus);
		return resultstatus;
	}
	
	/** send the sendTestConnectionInfo to the agent   */
	public String CheckTestConnResult(CheckTestConnectionInfo msg) throws CreateException,FinderException{
		PreparedStatement preparedStatement=null;
		Connection connection=null;
		String address = null;
		String tableoid = null;
		String mib = null;
		String communitystrg = null;
		String resultUrl= null;
		String checkstatus=null;
		String checkresult=null;
		String agentId=msg.getMsg();
		try{
			LOGGER.info("ManagerDao :: CheckTestConnResult : start ... ");
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				    preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_TEST_CON_CHECK_DETAILS"));
				    preparedStatement.setString(1,agentId);
				    ResultSet rs = preparedStatement.executeQuery();
					if (rs.next()) {
						agentId=rs.getString(1);
						 address = rs.getString(2);
						 tableoid = rs.getString(3);
						 mib = rs.getString(4);
						 communitystrg = rs.getString(5);
						 checkstatus = rs.getString(6);
						 checkresult = rs.getString(7);
						} 
					
					resultUrl="checkstatus="+checkstatus+"&checkresult="+checkresult;
					
			}else{
				resultUrl="";
				LOGGER.error("ManagerDao :: CheckTestConnResult : got error resultUrl ... "+resultUrl);
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			resultUrl="";
			LOGGER.error("ManagerDao :: CheckTestConnResult : got error resultUrl ... "+resultUrl);
			throw (CreateException)handleException(e, "Create", "Unable to execute sendTestConnectionStatus select query", "Error occured while executing sendTestConnectionStatus select query ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("ManagerDao :: CheckTestConnResult : return resultUrl ... "+resultUrl);
		return resultUrl;
	}

	
	/** send the sendTestConnectionInfo to the agent   */
	public String SendTestConnResponse(String agentId) throws CreateException,FinderException{
		PreparedStatement preparedStatement=null;
		CheckTestConnectionInfo checktestconinfo = null;
		Connection connection=null;
		String checkresult=null;
		String checkstatus=null;
		
		try{
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				    preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_TEST_CON_CHECK_DETAILS"));
				    preparedStatement.setString(1,agentId);
				    ResultSet rs = preparedStatement.executeQuery();
					if (rs.next()) {
						checkstatus = rs.getString(6);
						checkresult = rs.getString(7)==""?null:rs.getString(7);
						} 
					checktestconinfo=new CheckTestConnectionInfo(agentId);
					checktestconinfo.setCheckStatus(checkstatus);
					checktestconinfo.setCheckResult(checkresult);
					LOGGER.info(" ManagerDao:: SendTestConnResponse : getCheckStatus ...... "+checktestconinfo.getCheckStatus());
					LOGGER.info(" ManagerDao:: SendTestConnResponse : getCheckResult .....  "+checktestconinfo.getCheckResult());
			            
			}else{
				checkresult="";
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			checkresult="";
			throw (CreateException)handleException(e, "Create", "Unable to execute sendTestConnectionStatus select query", "Error occured while executing sendTestConnectionStatus select query ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		
		LOGGER.info(" ManagerDao:: SendTestConnResponse : getCheckResult ..... return checkk result :"+checkresult);
		return checkresult;
	}
	
	public static Map<String,String> getKnownErrors(){
		Map<String,String> treeMap=null;
		Statement statement=null;
		Connection connection=null;
		try {
			
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
			String sqlQuery="select * from ms_agent_known_errors";
			//connection.setAutoCommit(false);
			statement = connection.createStatement();
			ResultSet rs = (ResultSet) statement.executeQuery(sqlQuery);
				treeMap=new TreeMap<String,String>();
			
			if(treeMap!=null){
			while(rs.next()){
				String name=rs.getString("name");
				String value=rs.getString("value");
					treeMap.put(name, value);
			  }
			}
		}
			
	} catch (SQLException e) {
			try {
				connection.rollback();
				if(statement!=null)
				statement.close();
			} catch (SQLException e1) {
				LOGGER.error("",e1);
			}
			LOGGER.error("",e);
		}finally{
				try {
					if(statement!=null)
						statement.close();
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return treeMap;
	}
	
}
	

