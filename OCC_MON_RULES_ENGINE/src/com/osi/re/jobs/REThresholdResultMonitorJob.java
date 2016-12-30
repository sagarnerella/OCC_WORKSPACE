package com.osi.re.jobs;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.common.util.MSRegularExpressions;
import com.osi.re.dao.IREDao;
import com.osi.re.dao.REDao;
import com.osi.re.vo.CheckpointResult;

public class REThresholdResultMonitorJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(REThresholdResultMonitorJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("REThresholdResultMonitorJob fired at :: "+Calendar.getInstance().getTime());
		LOGGER.info("REAlertIncidentsMonitorJob fired at UTC Time:: "+Timestamp.valueOf(formatDate(getExecTimeinUTC())));
		List<Integer> ctresultIds = null;
		List<Integer> rpresultIds = null;
		IREDao reDao = null;
		List<CheckpointResult> checkpointResults = null;
		try {
			reDao = new REDao();
			checkpointResults = reDao.fetchResults("T");
			if (null != checkpointResults && checkpointResults.size() > 0) {
				ctresultIds = new ArrayList<Integer>(0);
				rpresultIds = new ArrayList<Integer>(0);
				for (CheckpointResult checkpointResult : checkpointResults) {
					ctresultIds.add(checkpointResult.getResultId());
				}
			}
			if (null != ctresultIds && ctresultIds.size() > 0) {
				reDao.updateResultProcessStatus(ctresultIds, "I");
			}
			for (CheckpointResult checkpointResult : checkpointResults) {
				if (MSRegularExpressions.calculateRelationalOperators(checkpointResult.getFormatedValue(), checkpointResult.getThreasholdValue(), checkpointResult.getThreasholdCondition())) {
					reDao.updateCheckpointAlert(checkpointResult.getDeviceId(), checkpointResult.getCheckpointId());
					rpresultIds.add(checkpointResult.getResultId());
					//reDao.updateResultProcessStatus(checkpointResult.getResultId());
				} else {
					reDao.updateCheckpointAlertStatus(checkpointResult.getDeviceId(), checkpointResult.getCheckpointId(), "ABANDONED");
					rpresultIds.add(checkpointResult.getResultId());
					//reDao.updateResultProcessStatus(checkpointResult.getResultId());
				}
			}
			if (null != rpresultIds && rpresultIds.size() > 0) {
				reDao.updateResultProcessStatus(rpresultIds,"P");
			}
			LOGGER.info("Fired REThresholdResultMonitorJob "+checkpointResults.toString());
		} catch (Exception e) {
			try {
				if (null != reDao && null != rpresultIds && rpresultIds.size() > 0) {
					reDao.updateResultProcessStatus(rpresultIds,"P");
				}
				if (null != reDao && null != ctresultIds && ctresultIds.size() > 0){
					reDao.updateIntermediateResultProcess(ctresultIds);
				}
			} catch (Exception e1) {
				LOGGER.error("",e1);
			}
			LOGGER.error("Error occurred while executing REThresholdResultMonitorJob",e);
		}
		LOGGER.info("REThresholdResultMonitorJob completed execution at :: "+Calendar.getInstance().getTime());
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
