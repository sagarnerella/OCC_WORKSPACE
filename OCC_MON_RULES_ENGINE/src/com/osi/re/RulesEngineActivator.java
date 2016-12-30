package com.osi.re;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.osi.common.util.LoadKnowErrors;
import com.osi.re.common.CommonUtilities;
import com.osi.scheduler.MSScheduler;

public class RulesEngineActivator {
	private static final Logger LOGGER = Logger.getLogger(RulesEngineActivator.class);
	static {
        try {
            // load RElog4j properties
        	PropertyConfigurator.configure(System.getProperty("user.dir")+ "/RElog4j.properties");
        }
        catch (Exception ex) {
            // Make sure you log the exception, as it might be swallowed
        	LOGGER.info("failed to initialize log4j" + ex.getMessage());
        }
    }
	public static void main(String[] args) {
		try {
			LoadKnowErrors.getKnowErrors();
			//Rules engine regular monitoring job 
			JobDetail reRMConfigJob =  MSScheduler.createJob("RERMJob", "RERMJobGroup", com.osi.re.jobs.RERegularResultMonitorJob.class);
			SimpleTrigger reRMConfigTrigger = MSScheduler.createSimpleTrigger("RERMTrigger", "RERMTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_RESULT_REGULAR_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reRMConfigJob, reRMConfigTrigger);
			// Rules engine threshold monitoring job
			JobDetail reTMConfigJob =  MSScheduler.createJob("RETMJob", "RETMJobGroup", com.osi.re.jobs.REThresholdResultMonitorJob.class);
			SimpleTrigger reTMConfigTrigger = MSScheduler.createSimpleTrigger("RETMTrigger", "RETMTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_RESULT_THRESHOLD_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reTMConfigJob, reTMConfigTrigger);
			// Rules engine alert monitoring job
			JobDetail reAMConfigJob =  MSScheduler.createJob("REAMJob", "REAMJobGroup", com.osi.re.jobs.REAlertMonitorJob.class);
			SimpleTrigger reAMConfigTrigger = MSScheduler.createSimpleTrigger("REAMTrigger", "REAMTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_RESULT_ALERT_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reAMConfigJob, reAMConfigTrigger);
			// Rules engine alert incident monitoring job
			JobDetail reAIMConfigJob =  MSScheduler.createJob("REAIMJob", "REAIMJobGroup", com.osi.re.jobs.REAlertIncidentsMonitorJob.class);
			SimpleTrigger reAIMConfigTrigger = MSScheduler.createSimpleTrigger("REAIMTrigger", "REAIMTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_RESULT_ALERT_INCIDENT_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reAIMConfigJob, reAIMConfigTrigger);
			// Rules engine device unavailability  check monitoring job
			JobDetail reDUCConfigJob =  MSScheduler.createJob("REDUCJob", "REDUCJobGroup", com.osi.re.jobs.DeviceUnavailibilityCheckJob.class);
			SimpleTrigger reDUCConfigTrigger = MSScheduler.createSimpleTrigger("REDUCTrigger", "REDUCTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_RESULT_DEVICE_UNAVAILABILITY_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reDUCConfigJob, reDUCConfigTrigger);
			// Rules engine agent error monitoring job
			JobDetail reAEConfigJob =  MSScheduler.createJob("REAECJob", "REAECJobGroup", com.osi.re.jobs.AgentErrorMonitorJob.class);
			SimpleTrigger reAECConfigTrigger = MSScheduler.createSimpleTrigger("REAECTrigger", "REAECTriggerGroup", Integer.parseInt(CommonUtilities.getProperty("MS_AGENT_ERROR_MONITORING_FREQUENCY")));
			MSScheduler.scheduleJobWithSimpleTrigger(reAEConfigJob, reAECConfigTrigger);
			
		} catch (NumberFormatException e) {
			LOGGER.error("",e);
		} catch (SchedulerException e) {
			LOGGER.error("",e);
		}
		
	}
}
