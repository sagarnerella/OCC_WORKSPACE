package com.osi.re.jobs;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.re.common.CommonUtilities;
import com.osi.re.dao.IREDao;
import com.osi.re.dao.REDao;
import com.osi.re.exception.FinderException;
import com.osi.re.vo.CheckpointAlert;

public class REAlertMonitorJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(REAlertMonitorJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("REAlertMonitorJob fired at :: "+Calendar.getInstance().getTime());
		String ALERT_STATUS = CommonUtilities.getProperty("ACTIVE_ALERT_STATUS");
		List<CheckpointAlert> checkpointAlerts = null;
		try {
			IREDao reDao = new REDao();
			checkpointAlerts = reDao.getCheckpointAlerts(); // with status Active or Published
			
			for (CheckpointAlert checkpointAlert : checkpointAlerts) {
				if (checkpointAlert.getThresholdExceededCounter() >= checkpointAlert.getRepeatCount()) {
					reDao.updateCheckpointAlertStatus(checkpointAlert.getDeviceId(), checkpointAlert.getDeviceCheckpointId(), "RAISE_INCIDENT");
				} else {
					if (checkpointAlert.getAlertStatus().equalsIgnoreCase(ALERT_STATUS)) {
						reDao.savePublisherAndAlertStatus(checkpointAlert,checkpointAlert.getDeviceId(), checkpointAlert.getDeviceCheckpointId(), "PUBLISHED");
					}
				}
			}
			LOGGER.info("Fired REAlertMonitorJob "+checkpointAlerts.toString());
		} catch(FinderException fe){
			LOGGER.error("Error occurred while executing REAlertMonitorJob",fe);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing REAlertMonitorJob",e);
		}
		LOGGER.info("REAlertMonitorJob completed execution at :: "+Calendar.getInstance().getTime());
	}

}
