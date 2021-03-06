package com.osi.re.jobs;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.re.common.CommonUtilities;
import com.osi.re.dao.IREDao;
import com.osi.re.dao.REDao;
import com.osi.re.exception.FinderException;
import com.osi.re.vo.CheckpointResult;
import com.osi.re.vo.MSAgent;
import com.osi.re.vo.MSAgentPollAudit;

public class DeviceUnavailibilityCheckJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(DeviceUnavailibilityCheckJob.class);
	
	private static final String ROWSET_START = "<ROWSET>";
	private static final String ROW_START = "<ROW>";
	private static final String RESULT_VALUE_START = "<Result_Value>";
	private static final String DESCRIPTION_START = "<Description>";
	private static final String ROWSET_END = "</ROWSET>";
	private static final String ROW_END = "</ROW>";
	private static final String RESULT_VALUE_END = "</Result_Value>";
	private static final String DESCRIPTION_END = "</Description>";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("DeviceUnavailibilityCheckJob fired at :: "+Calendar.getInstance().getTime());
		LOGGER.info("REAlertIncidentsMonitorJob fired at UTC Time:: "+Timestamp.valueOf(formatDate(getExecTimeinUTC())));
		List<CheckpointResult> checkpointResults = null;
		List<MSAgentPollAudit> msAgentPollAudits = null;
		List<MSAgent> msAgents = null;
		
		try {
			IREDao reDao = new REDao();
			msAgents = reDao.getAgentsList();
			
			for (MSAgent msAgent : msAgents) {
				int pollid=0;
				String error_type=null;
				Timestamp pollAuditCreationTime = null;
				msAgentPollAudits = reDao.getAgentPollingDetails(msAgent.getAgentId());
				for (MSAgentPollAudit msAgentPollAudit : msAgentPollAudits) {
					
					pollid=msAgentPollAudit.getPollId();
					error_type=msAgentPollAudit.getErrorType();
					pollAuditCreationTime=msAgentPollAudit.getCreation_date();
					CheckpointResult checkpointResult=new CheckpointResult();
					checkpointResult.setDeviceId(msAgent.getDeviceId());
				
					checkpointResult.setCheckpointExecutionTime(Timestamp.valueOf(formatDate(getExecTimeinUTC())));
					String resultDescription = ROWSET_START+ROW_START+RESULT_VALUE_START+CommonUtilities.getProperty("AGENT_UNAVAILABLE_DESCRIPTION")+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+msAgent.getAgentName()+DESCRIPTION_END+ROW_END+ROWSET_END;
					checkpointResult.setResultDesc(resultDescription);
					            reDao.saveCheckpointAlert(checkpointResult,"RAISE_INCIDENT");
					checkpointResult.setErrorType(error_type);
					checkpointResult.setPollid(pollid);
					checkpointResult.setPollAuditCreationTime(pollAuditCreationTime);
					reDao.updatePollAuditStatus(msAgent.getAgentId(), checkpointResult);
					LOGGER.info("Raised incident on agent"+msAgent.getAgentName());
				}
			}
			
			checkpointResults = reDao.fetchResults("E");
			for (CheckpointResult checkpointResult : checkpointResults) {
				String resultDesc ="";
				if(!CommonUtilities.getProperty("SNMP_TABLE").equalsIgnoreCase(checkpointResult.getConnectType()))
				{
					resultDesc=ROWSET_START+ROW_START+RESULT_VALUE_START+CommonUtilities.getProperty("EXCEPTION_RESULT_VALUE_DESCRIPTION")+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointResult.getAssetName()+DESCRIPTION_END+ROW_END+ROWSET_END;
				}else{
					resultDesc=ROWSET_START+ROW_START+RESULT_VALUE_START+checkpointResult.getValue()+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointResult.getAssetName()+DESCRIPTION_END+ROW_END+ROWSET_END;
				}
				checkpointResult.setResultDesc(resultDesc);
				reDao.saveCheckpointAndupdateResult(checkpointResult,"RAISE_INCIDENT");
			}
			
			LOGGER.info("Fired DeviceUnavailibilityCheckJob "+checkpointResults.toString());
		
		} catch(FinderException fe){
			LOGGER.error("Error occurred while executing DeviceUnavailibilityCheckJob",fe);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing DeviceUnavailibilityCheckJob",e);
		}
		LOGGER.info("DeviceUnavailibilityCheckJob completed execution at :: "+Calendar.getInstance().getTime());
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
	
	private String formatDate(long executionTime){
		Date date=new Date(executionTime);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df2.format(date);
	}

}
