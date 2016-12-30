package com.osi.re.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.osi.common.config.constants.MSConstants;
import com.osi.common.util.MSConnectionManager;
import com.osi.re.common.CommonUtilities;
import com.osi.re.exception.DBConnectException;
import com.osi.re.exception.FinderException;
import com.osi.re.exception.PersistException;
import com.osi.re.vo.CheckpointAlert;
import com.osi.re.vo.CheckpointResult;
import com.osi.re.vo.MSAgent;
import com.osi.re.vo.MSAgentPollAudit;

public class REDao implements IREDao{
	private static final Logger LOGGER = Logger.getLogger(REDao.class);

	@Override
	public List<CheckpointResult> fetchResults(String checkType) throws FinderException {
		LOGGER.info("REDao::fetchResults:Start");
		List<CheckpointResult> checkpointResults = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		CheckpointResult checkpointResult = null;
		
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				String dateString = getDateString(connection);
				StringBuffer query = new StringBuffer();
				query.append("select mdcpr.result_id, mdcpr.value, mdcpr.formated_value, mdcpr.checkpoint_id, mdcpr.device_id, mdcpr.check_type,oct.ct_name, ");
				query.append(" mdcpr.process_status, mdct.threshold_value, mdct.threshold_condition, mdct.recheck_interval, mdct.repeat_count,mdcpr.checkpoint_execution_time, oa.asset_name,mdcpr.resultDesc from ms_device_checkpoint mdc,osi_connect_types oct,osi_check_assetrel_conntype_map cacm ,");
				query.append(" ms_device_checkpoint_result_"+dateString+" mdcpr, ms_device_checkpoint_threshold mdct, ms_device_configuration mdconfig, osi_assets oa where mdcpr.device_id = mdct.device_id and mdc.device_checkpoint_id=mdcpr.checkpoint_id  and mdc.cacm_id=cacm.cacm_id and cacm.cacm_ct_id=oct.ct_id");
				query.append(" and mdc.device_checkpoint_id=mdct.device_checkpoint_id and mdcpr.checkpoint_id = mdct.device_checkpoint_id and mdconfig.device_id = mdc.device_id and oa.asset_id = mdconfig.asset_id and mdcpr.process_status = 'N' and mdcpr.check_type = ?");
				preparedStatement = connection.prepareStatement(query.toString());
				preparedStatement.setString(1, checkType);
				resultSet = preparedStatement.executeQuery();
				checkpointResults = new ArrayList<CheckpointResult>(0);
				while(resultSet.next()){
					checkpointResult = new CheckpointResult();
					checkpointResult.setResultId(resultSet.getInt("result_id"));
					checkpointResult.setValue(resultSet.getString("value"));
					checkpointResult.setFormatedValue(resultSet.getString("formated_value"));
					checkpointResult.setCheckpointId(resultSet.getInt("checkpoint_id"));
					checkpointResult.setDeviceId(resultSet.getInt("device_id"));
					checkpointResult.setCheckType(resultSet.getString("check_type").charAt(0));
					checkpointResult.setProcessstatus(resultSet.getString("process_status").charAt(0));
					checkpointResult.setThreasholdValue(resultSet.getString("threshold_value"));
					checkpointResult.setThreasholdCondition(resultSet.getString("threshold_condition"));
					checkpointResult.setRecheckInterval(resultSet.getInt("recheck_interval"));
					checkpointResult.setRepeatCount(resultSet.getInt("repeat_count"));
					checkpointResult.setAssetName(resultSet.getString("asset_name"));
					
					checkpointResult.setConnectType(resultSet.getString("ct_name"));
					checkpointResult.setCheckpointExecutionTime(resultSet.getTimestamp("checkpoint_execution_time"));
					//Added for SNMPTable
					checkpointResult.setResultDesc(resultSet.getString("resultDesc"));
					checkpointResults.add(checkpointResult);	
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		}catch (MySQLSyntaxErrorException e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Table doesnot exist", null);
		}catch (SQLException e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Unable to load datasource", null);
		}catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		LOGGER.info("REDao::fetchResults:End");
		return checkpointResults;
	}


	@Override
	public boolean updateResultProcessStatus(List<Integer> resultIds, String checkType) throws FinderException {
		LOGGER.info("REDao::updateResultProcessStatus:Start");
		boolean updateStatus = false;
		Connection connection = null;
		PreparedStatement  preparedStatement = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				String dateString = getDateString(connection);
				StringBuffer sb = new StringBuffer("update ms_device_checkpoint_result_"+dateString+" set process_status=? where result_id in (");
				for( Integer rid : resultIds ){
					sb.append(""+rid+",");
				}
				String sqlBase = sb.toString().substring( 0, sb.toString().length()-1)+");";
				LOGGER.info(sqlBase);
				preparedStatement = connection.prepareStatement(sqlBase);
				preparedStatement.setString(1, checkType);
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("REDao::updateResultProcessStatus:End");
		return updateStatus;
	}

	@Override
	public boolean updateIntermediateResultProcess(List<Integer> resultIds) throws FinderException {
		LOGGER.info("REDao::updateIntermediateResultProcess:Start");
		boolean updateStatus = false;
		Connection connection = null;
		PreparedStatement  preparedStatement = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				String dateString = getDateString(connection);
				StringBuffer sb = new StringBuffer("update ms_device_checkpoint_result_"+dateString+" set process_status='N' where process_status='I' and result_id in (");
				for( Integer rid : resultIds ){
					sb.append(""+rid+",");
				}
				String sqlBase = sb.toString().substring( 0, sb.toString().length()-1)+");";
				LOGGER.info(sqlBase);
				preparedStatement = connection.prepareStatement(sqlBase);
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("REDao::updateIntermediateResultProcess:End");
		return updateStatus;
	}

	@Override
	public boolean updateResultProcessStatus(int resultId) throws FinderException {
		LOGGER.info("REDao::updateResultProcessStatus:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateFlag = resultProcessStatusUpdate(resultId, connection);
				if (updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateResultProcessStatus:End");
		return updateStatus;
	}
	
	
	@Override
	public boolean saveCheckpointAlert(CheckpointResult checkpointResult, String alertStatus) throws FinderException {
		LOGGER.info("REDao::saveCheckpointAlert:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean alertFalg = insertCheckpointAlert( checkpointResult, alertStatus, connection);
				if(alertFalg) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::saveCheckpointAlert:End");
		return updateStatus;
	}
	
	@Override
	public boolean updateCheckpointAlert(int deviceId, int checkpointId) throws FinderException {
		LOGGER.info("REDao::updateCheckpointAlert:Start");
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_CHECKPOINT_ALERTS"));
				preparedStatement.setString(1,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
				preparedStatement.setInt(2, deviceId);
				preparedStatement.setInt(3, checkpointId);
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("REDao::updateCheckpointAlert:End");
		return updateStatus;
	}

	@Override
	public boolean updateCheckpointAlertStatus(int deviceId, int checkpointId, String alertStatus) throws FinderException {
		LOGGER.info("REDao::updateCheckpointAlert:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateFlag = updateAlertStatus(deviceId, checkpointId, alertStatus, connection);
				if (updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateCheckpointAlert:End");
		return updateStatus;
	}
	
	
	private boolean updateAlertStatus(int deviceId, int checkpointId, String alertStatus, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_CHECKPOINT_ALERTS_STATUS"));
			preparedStatement.setString(1, alertStatus);
			preparedStatement.setString(2,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(3, deviceId);
			preparedStatement.setInt(4, checkpointId);
			preparedStatement.execute();
			updateStatus = true;
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}

	@Override
	public boolean updateCheckpointAlertIncidentStatus(int checkpointAlertID, String alertStatus) throws FinderException {
		LOGGER.info("REDao::updateCheckpointAlert:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateFlag = updateCheckpointIncidentStatus(checkpointAlertID, alertStatus, connection);
				if (updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateCheckpointAlert:End");
		return updateStatus;
	}
	
	private boolean updateCheckpointIncidentStatus(int checkpointAlertID, String alertStatus, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_CHECKPOINT_ALERT_INCIDENT_STATUS"));
			preparedStatement.setString(1, alertStatus);
			preparedStatement.setString(2,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(3, checkpointAlertID);
			preparedStatement.execute();
			updateStatus = true;
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}

	@Override
	public boolean checkCheckpointAlertEntry(int deviceId, int checkpointId) throws FinderException {
		LOGGER.info("REDao::checkCheckpointAlertEntry:Start");
		boolean updateStatus = false;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_GET_CHECKPOINT_ALERT"));
				preparedStatement.setInt(1, deviceId);
				preparedStatement.setInt(2, checkpointId);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					updateStatus=true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		LOGGER.info("REDao::checkCheckpointAlertEntry:End");
		return updateStatus;
	}

	@Override
	public List<CheckpointAlert> getCheckpointAlerts() throws FinderException {
		LOGGER.info("REDao::getCheckpointAlerts:Start");
		List<CheckpointAlert> checkpointAlerts = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		CheckpointAlert checkpointAlert = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_ALERTS"));
				resultSet = preparedStatement.executeQuery();
				checkpointAlerts = new ArrayList<CheckpointAlert>(0);
				while(resultSet.next()){
					checkpointAlert = new CheckpointAlert();
					checkpointAlert.setCheckpointAlertId(resultSet.getInt("checkpoint_alert_id"));
					checkpointAlert.setDeviceId(resultSet.getInt("device_id"));
					checkpointAlert.setDeviceCheckpointId(resultSet.getInt("device_checkpoint_id"));
					checkpointAlert.setThresholdExceededCounter(resultSet.getInt("threshold_exceeded_counter"));
					checkpointAlert.setAlertStatus(resultSet.getString("alert_status"));
					checkpointAlert.setAgentId(resultSet.getInt("agent_id"));
					checkpointAlert.setRepeatCount(resultSet.getInt("repeat_count"));
					checkpointAlerts.add(checkpointAlert);	
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		LOGGER.info("REDao::getCheckpointAlerts:End");
		return checkpointAlerts;
	}

	@Override
	public List<CheckpointAlert> getCheckpointIncidentAlerts() throws FinderException {
		LOGGER.info("REDao::getCheckpointIncidentAlerts:Start");
		List<CheckpointAlert> checkpointAlerts = null;
		ResultSet resultSet = null;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		CheckpointAlert checkpointAlert = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_RETRIEVE_CHECKPOINT_INCIDENT_ALERTS"));
				resultSet = preparedStatement.executeQuery();
				checkpointAlerts = new ArrayList<CheckpointAlert>(0);
				while(resultSet.next()){
					checkpointAlert = new CheckpointAlert();
					checkpointAlert.setCheckpointAlertId(resultSet.getInt("checkpoint_alert_id"));
					checkpointAlert.setDeviceId(resultSet.getInt("device_id"));
					checkpointAlert.setDeviceCheckpointId(resultSet.getInt("device_checkpoint_id"));
					checkpointAlert.setAssetId(resultSet.getInt("asset_id"));
					checkpointAlert.setAssetName(resultSet.getString("asset_name"));
					checkpointAlert.setResultId(resultSet.getInt("result_id"));
					checkpointAlert.setResultDesc(resultSet.getString("result_desc"));
					checkpointAlert.setCheckpointExecutionTime(resultSet.getTimestamp("checkpoint_execution_time"));
					checkpointAlerts.add(checkpointAlert);	
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		LOGGER.info("REDao::getCheckpointIncidentAlerts:End");
		return checkpointAlerts;
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
				resultSet = statement.executeQuery("select agent_id, agent_name, device_id from ms_agent where is_active = 1");
				agentsList = new ArrayList<MSAgent>(0);
				while(resultSet.next()){
					msAgent = new MSAgent();
					msAgent.setAgentId(resultSet.getInt("agent_id"));
					msAgent.setAgentName(resultSet.getString("agent_name"));
					msAgent.setDeviceId(resultSet.getInt("device_id"));
					agentsList.add(msAgent);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, statement, resultSet);
		}
		return agentsList;
	}

	@Override
	public List<MSAgentPollAudit> getAgentPollingDetails(int agentID) throws FinderException {
		List<MSAgentPollAudit> msAgentPollAudits = null;
		MSAgentPollAudit msAgentPollAudit = null;
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				statement = connection.createStatement();
				String errortype="AGENT_UNAVAILABLE";
				String status="Z";
				resultSet = statement.executeQuery("select poll_id, status,error_type,creation_date from ms_agent_poll_audit where agent_id="+agentID+" and error_type='"+errortype+"' and status='"+status+"' order by poll_id desc ");
				msAgentPollAudits = new ArrayList<MSAgentPollAudit>(0);
				while(resultSet.next()){
					msAgentPollAudit = new MSAgentPollAudit();
					msAgentPollAudit.setPollId(resultSet.getInt("poll_id"));
					msAgentPollAudit.setErrorType(resultSet.getString("error_type"));
					if (null != resultSet.getString("status"))
						msAgentPollAudit.setStatus(resultSet.getString("status").charAt(0));
					if (null != resultSet.getTimestamp("creation_date"))
						msAgentPollAudit.setCreation_date(resultSet.getTimestamp("creation_date"));
					msAgentPollAudits.add(msAgentPollAudit);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, statement, resultSet);
		}
		return msAgentPollAudits;
	}

	@Override
	public boolean savePublisherEntry(CheckpointAlert checkpointAlert) throws FinderException {
		LOGGER.info("REDao::updateCheckpointAlert:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean insertFalg = createPublisherEntry(checkpointAlert, connection);
				if (insertFalg) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateCheckpointAlert:End");
		return updateStatus;
	}
	
	
	private boolean createPublisherEntry(CheckpointAlert checkpointAlert, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_PUBLISHER_ENTRY"));
			preparedStatement.setString(1, CommonUtilities.getProperty("ALERT_PUBLISH_TYPE"));
			preparedStatement.setInt(2, checkpointAlert.getDeviceId());
			preparedStatement.setInt(3, checkpointAlert.getAgentId());
			preparedStatement.setString(4,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setString(5,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(6, checkpointAlert.getDeviceCheckpointId());
			preparedStatement.execute();
			preparedStatement.close();
			updateStatus = true;
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}
	
	

	@Override
	public boolean saveAlertIncidents(int assetId, int checkpointMasterId, int checkpointAlertId) throws FinderException {
		LOGGER.info("REDao::saveAlertIncidents:Start");
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_ALERT_INCIDENTS"));
				preparedStatement.setInt(1, assetId);
				if(checkpointMasterId != 0) {
					preparedStatement.setInt(2, checkpointMasterId);
				} else {
					preparedStatement.setNull(2, Types.NULL);
				}
				preparedStatement.setInt(3, checkpointAlertId);
				preparedStatement.setString(4,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
				preparedStatement.execute();
				connection.commit();
				updateStatus = true;
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, null);
		}
		LOGGER.info("REDao::saveAlertIncidents:End");
		return updateStatus;
	}

	@Override
	public boolean savePublisherAndAlertStatus(CheckpointAlert checkpointAlert, int deviceId, int deviceCheckpointId, String alertStatus) throws FinderException {
		LOGGER.info("REDao::savePublisherAndAlertStatus:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean insertFalg = createPublisherEntry(checkpointAlert, connection);
				boolean updateFlag = updateAlertStatus(checkpointAlert.getDeviceId(), checkpointAlert.getDeviceCheckpointId(), alertStatus, connection);
				if (insertFalg && updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::savePublisherAndAlertStatus:End");
		return updateStatus;
	}
	
	@Override
	public boolean updateAlertIncidents(int incidentId, int checkpointAlertId) throws FinderException {
		LOGGER.info("REDao::updateAlertIncidents:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateFalg = updateAlertIncidents(incidentId, checkpointAlertId, connection);
				if (updateFalg) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateAlertIncidents:End");
		return updateStatus;
	}
	
	private boolean updateAlertIncidents(int incidentId, int checkpointAlertId, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_ALERT_INCIDENTS"));
			preparedStatement.setInt(1, incidentId);
			preparedStatement.setString(2,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(3, checkpointAlertId);
			preparedStatement.execute();
			updateStatus = true;
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}

	@Override
	public boolean updateAlertIncidentsAndAlertStatus(int incidentId, int checkpointAlertId, String alertStatus) throws FinderException {
		LOGGER.info("REDao::updateAlertIncidentsAndAlertStatus:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean uAlertFalg = updateAlertIncidents(incidentId, checkpointAlertId, connection);
				boolean uIncidentFlag = updateCheckpointIncidentStatus(checkpointAlertId, alertStatus, connection);
				if (uAlertFalg && uIncidentFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateAlertIncidentsAndAlertStatus:End");
		return updateStatus;
	}
	
	@Override
	public boolean updateAlertIncidentsErrorText(String errorText, int checkpointAlertId) throws FinderException {
		LOGGER.info("REDao::updateAlertIncidentsErrorText:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateErrorFlag = updateAlertIncidentsError(errorText, checkpointAlertId, connection);
				if (updateErrorFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateAlertIncidentsErrorText:End");
		return updateStatus;
	} 

	private boolean updateAlertIncidentsError(String errorText, int checkpointAlertId, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("UPDATE_ALERT_INCIDENTS_ERRORS"));
			preparedStatement.setString(1, errorText);
			preparedStatement.setString(2,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(3, checkpointAlertId);
			preparedStatement.execute();
			updateStatus = true;

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}
	
	@Override
	public boolean updateIncidentsErrorTextAndCheckpointAlertStatus(String errorText, int checkpointAlertId, String alertStatus) throws FinderException {
		LOGGER.info("REDao::updateIncidentsErrorTextAndCheckpointAlertStatus:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean updateErrorFlag = updateAlertIncidentsError(errorText, checkpointAlertId, connection);
				boolean updateFlag = updateCheckpointIncidentStatus(checkpointAlertId, alertStatus, connection);
				if (updateErrorFlag && updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::updateIncidentsErrorTextAndCheckpointAlertStatus:End");
		return updateStatus;
	}

	@Override
	public boolean verifyAlertIncident(int checkpointAlertId) throws FinderException {
		LOGGER.info("REDao::verifyAlertIncident:Start");
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("VERIFY_ALERT_INCIDENT"));
				preparedStatement.setInt(1, checkpointAlertId);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					updateStatus=true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		LOGGER.info("REDao::verifyAlertIncident:End");
		return updateStatus;
	}

	public int getChecckPointMasterId(int deviceCheckpointId) throws FinderException {
		int checkpointMasterId = 0;
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				statement = connection.createStatement();
				resultSet = statement.executeQuery("select oc.chk_scat_id chk_id from ms_device_checkpoint mdc, osi_check_assetrel_conntype_map ocscm, osi_check_assettype_rel_map ocarm,"
						+" osi_check_assettype_map ocam, osi_checks oc  where mdc.cacm_id = ocscm.cacm_id and ocscm.cacm_carm_id = ocarm.carm_id"
						+" and ocarm.carm_chk_id = ocam.catm_chk_id and ocam.catm_chk_id = oc.chk_id and mdc.device_checkpoint_id  = "+deviceCheckpointId);
				if(resultSet.next()){
					checkpointMasterId = resultSet.getInt("chk_id");
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, statement, resultSet);
		}
		return checkpointMasterId;
	}

	@Override
	public boolean saveCheckpointAndupdateResult(CheckpointResult checkpointResult, String alertStatus) throws FinderException {
		LOGGER.info("REDao::saveCheckpointAndupdateResult:Start");
		boolean updateStatus = false;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				boolean alertFalg = insertCheckpointAlert(checkpointResult, alertStatus, connection);
				boolean updateFlag = resultProcessStatusUpdate(checkpointResult.getResultId(), connection);
				if(alertFalg && updateFlag) {
					connection.commit();
					updateStatus = true;
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, null, null);
		}
		LOGGER.info("REDao::saveCheckpointAndupdateResult:End");
		return updateStatus;
	}
	
	private Exception handleException(Exception e, String exceptionType, String sqlExceptionMessage, String globalExceptionMessage, Connection connection){
		Exception exception=null;
		if("Finder".equalsIgnoreCase(exceptionType)){
			if(e instanceof DBConnectException){
				DBConnectException dbException=(DBConnectException)e;
				exception=new FinderException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
			} else if(e instanceof SQLException){
				exception=new FinderException(MSConstants.ERROR_CODE_7002, sqlExceptionMessage, e);
			}else if(e instanceof FinderException){
				FinderException fException=(FinderException)e;
				exception=new FinderException(fException.getCode(), fException.getUserMessage(), e);
			}else{
				exception=new FinderException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
			}
		} else if("Persist".equalsIgnoreCase(exceptionType)){
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
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			connection=null;
		}
	}
	
	private String getDateString(Connection conn) throws FinderException {
		String datestring = null;
		try {
			Statement st = conn.createStatement();
			ResultSet dresultSet = st.executeQuery("SELECT UTC_DATE() from dual");
			if (dresultSet.next()) {
				datestring = dresultSet.getString("UTC_DATE()");
			}
			datestring = datestring.replaceAll("-", "");
		} catch (SQLException e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return datestring;
	}
	
	private boolean resultProcessStatusUpdate(int resultId, Connection conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		try {
			String dateString = getDateString(conn);
			preparedStatement = conn.prepareStatement("update ms_device_checkpoint_result_"+dateString+" set process_status='P', updated_by=5, updated_date=sysdate() where result_id=?");
			preparedStatement.setInt(1, resultId);
			preparedStatement.execute();
			preparedStatement.close();
			updateStatus = true;

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}
	
	private boolean insertCheckpointAlert(CheckpointResult checkpointResult, String alertStatus, Connection  conn) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		
		try {
			preparedStatement = conn.prepareStatement(CommonUtilities.getProperty("QUERY_SAVE_CHECKPOINT_ALERTS"));
			preparedStatement.setInt(1, checkpointResult.getDeviceId());
			if(checkpointResult.getCheckpointId() != 0) {
				preparedStatement.setInt(2, checkpointResult.getCheckpointId());
			} else {
				preparedStatement.setNull(2, Types.NULL);
			}
			preparedStatement.setString(3, alertStatus);
			preparedStatement.setString(4,CommonUtilities.formatDate(CommonUtilities.getExecTimeinUTC()));
			preparedStatement.setInt(5, checkpointResult.getResultId());
			preparedStatement.setTimestamp(6, checkpointResult.getCheckpointExecutionTime());
			preparedStatement.setString(7, checkpointResult.getResultDesc());
			preparedStatement.execute();
			preparedStatement.close();
			updateStatus = true;
			
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}
		return updateStatus;
	}
	
	public boolean updatePollAuditStatus(int agentID, CheckpointResult checkpointResult) throws FinderException {
		boolean updateStatus = false;
		PreparedStatement  preparedStatement = null;
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
			
			preparedStatement = connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_POLL_AUDIT_STATUS"));
			preparedStatement.setString(1, "P");
			preparedStatement.setTimestamp(2, checkpointResult.getPollAuditCreationTime());
			preparedStatement.setInt(3, 1);
			preparedStatement.setTimestamp(4, checkpointResult.getCheckpointExecutionTime());
			preparedStatement.setInt (5, agentID);
			preparedStatement.setString(6, checkpointResult.getErrorType());
			preparedStatement.setInt(7, checkpointResult.getPollid());
			preparedStatement.executeUpdate();
			preparedStatement.close();
			updateStatus = true;
			}

		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while updating the poll audit status", null);
		}finally{
			closeDBConnection(connection, preparedStatement, resultSet);
		}
		return updateStatus;
	}
	
	@Override
	public List<MSAgentPollAudit> getAgentErrorPollingDetails(int agentID) throws FinderException {
		List<MSAgentPollAudit> msAgentPollAudits = null;
		MSAgentPollAudit msAgentPollAudit = null; 
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = MSConnectionManager.getConnection();
			if (null != connection) {
				statement = connection.createStatement();
				String errortype="AGENT_ERROR";
				String status="Z";
				resultSet = statement.executeQuery("select poll_id, status,error_type,creation_date from ms_agent_poll_audit where agent_id="+agentID+" and error_type='"+errortype+"' and status='"+status+"' order by poll_id desc ");
				msAgentPollAudits = new ArrayList<MSAgentPollAudit>(0);
				while(resultSet.next()){
					msAgentPollAudit = new MSAgentPollAudit();
					msAgentPollAudit.setPollId(resultSet.getInt("poll_id"));
					msAgentPollAudit.setErrorType(resultSet.getString("error_type"));
					if (null != resultSet.getString("status"))
						msAgentPollAudit.setStatus(resultSet.getString("status").charAt(0));
					if (null != resultSet.getTimestamp("creation_date"))
						msAgentPollAudit.setCreation_date(resultSet.getTimestamp("creation_date"));
					msAgentPollAudits.add(msAgentPollAudit);
				}
			}else{
				throw new DBConnectException(MSConstants.ERROR_CODE_7000, "Unable to establish db connection", "Connection is null");
			}
		} catch (Exception e) {
			throw (FinderException)handleException(e, "Finder", "Error occured while executing the query", "Error occured while parsing the result", null);
		}finally{
			closeDBConnection(connection, statement, resultSet);
		}
		return msAgentPollAudits;
	}
	public static Map<String,String> getKnownErrors(Connection connection){
		Map<String,String> treeMap=null;
		Statement statement=null;
		if(connection!=null){
		try {
			String sqlQuery="select *from ms_agent_known_errors";
			connection.setAutoCommit(false);
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
			connection.commit();
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
		}else{
			LOGGER.info("Please check the Connection Object  ");
		}
		return treeMap;
	}
}
