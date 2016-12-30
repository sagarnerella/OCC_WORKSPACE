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

import com.osi.re.dao.IREDao;
import com.osi.re.dao.REDao;
import com.osi.re.exception.FinderException;
import com.osi.re.exception.WSException;
import com.osi.re.vo.CheckpointAlert;
import com.osi.re.ws.client.REWSClient;
import com.osi.re.ws.stub.CreateIncidentResponseType;

public class REAlertIncidentsMonitorJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(REAlertIncidentsMonitorJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("REAlertIncidentsMonitorJob fired at :: "+Calendar.getInstance().getTime());
		LOGGER.info("REAlertIncidentsMonitorJob fired at UTC Time:: "+Timestamp.valueOf(formatDate(getExecTimeinUTC())));
		
		List<CheckpointAlert> checkpointAlerts = null;
		IREDao reDao = null;
		CreateIncidentResponseType createIncidentResponseType = null;
		try {
			reDao = new REDao();
			checkpointAlerts = reDao.getCheckpointIncidentAlerts();// with RAISE_INCIDENT status
			for (CheckpointAlert checkpointAlert : checkpointAlerts) {
				if(checkpointAlert.getDeviceCheckpointId() != 0) {
					int masterCheckpointId = reDao.getChecckPointMasterId(checkpointAlert.getDeviceCheckpointId());
					checkpointAlert.setMasterCheckpointId(masterCheckpointId);
				}
				boolean checkIncident = reDao.verifyAlertIncident(checkpointAlert.getCheckpointAlertId());
				if (!checkIncident) {
					reDao.saveAlertIncidents(checkpointAlert.getAssetId(), checkpointAlert.getMasterCheckpointId(), checkpointAlert.getCheckpointAlertId());
				}
				try {
					createIncidentResponseType = REWSClient.createWSRequest(checkpointAlert);
				} catch (WSException we) {
					reDao.updateIncidentsErrorTextAndCheckpointAlertStatus(we.getMessage(), checkpointAlert.getCheckpointAlertId(), "WS_ERROR");
					we.printStackTrace();
				}
				if (null != createIncidentResponseType) {
					if (null != createIncidentResponseType.getRetmsg() && !"".equalsIgnoreCase(createIncidentResponseType.getRetmsg())) {
						reDao.updateAlertIncidentsAndAlertStatus(Integer.parseInt(createIncidentResponseType.getRetmsg()), checkpointAlert.getCheckpointAlertId(), "INCIDENT_RAISED");
					} else if (null != createIncidentResponseType.getErrormsg() && !"".equalsIgnoreCase(createIncidentResponseType.getErrormsg())) {
						reDao.updateIncidentsErrorTextAndCheckpointAlertStatus(createIncidentResponseType.getErrormsg(), checkpointAlert.getCheckpointAlertId(), "WS_ERROR_RESPONSE");
					} else {
						reDao.updateIncidentsErrorTextAndCheckpointAlertStatus("Invalid Web Service Response", checkpointAlert.getCheckpointAlertId(), "WS_ERROR_RESPONSE");
					}
				}
			}
			LOGGER.info("Fired REAlertIncidentsMonitorJob "+checkpointAlerts.toString());
		} catch(FinderException fe){
			LOGGER.error("Error occurred while executing REAlertIncidentsMonitorJob",fe);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing REAlertIncidentsMonitorJob",e);
		}
		LOGGER.info("REAlertIncidentsMonitorJob completed execution at :: "+Calendar.getInstance().getTime());
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
