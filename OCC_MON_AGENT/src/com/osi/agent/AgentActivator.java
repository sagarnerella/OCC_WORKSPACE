/**
 * 
 */
package com.osi.agent;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.results.CacheManagerImpl;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.exception.OCCSchedulerException;
import com.osi.agent.scheduler.MSScheduler;
import com.osi.agent.scheduler.MSTrigger;


/**
 * @author OSI
 *
 */
public class AgentActivator {

	private static final Logger LOGGER = Logger.getLogger(AgentActivator.class);
	private static ICacheManager cacheManager = new CacheManagerImpl();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("AgentActivator.main() : START");
		
		try {

			
			//sendHeartBeatToManager();
			//getDeviceXmlFromManager();
			//getTestConnectionInfo();
						//getAlertConfigXml();
						//getUnSchedulerXmlFromManager();
			scheduleMonitoringJobs();
			//scheduleDeviceConfigJob ();
						//scheduleCheckpointResultsSerializerJob();
			//scheduleErrorMonitoringJob();
			//jsendResultToManager();
			
		

			LOGGER.info("AgentActivator.main() : END");
		} catch (OCCSchedulerException e) {
			LOGGER.info("AgentActivator.main() : ERROR");
			CommonUtilities.createErrorLogFile(e);
		}catch (Exception e) {
			LOGGER.info("AgentActivator.main() : ERROR");
			CommonUtilities.createErrorLogFile(e);
		}
	}
	static {
		try {
			// load log4j properties
			PropertyConfigurator.configure(System.getProperty("user.dir")+ "/log4j.properties");
		} catch (Exception ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Failed to initialize log4j" + ex);
		}
	}
	private static void sendHeartBeatToManager() throws SchedulerException{
		JobDetail sendHeartBeatToManagerJob = null;
		SimpleTrigger sendHeartBeatToManagerTrigger = null;
		try {
			
			LOGGER.info("at send heart start ");
			sendHeartBeatToManagerJob =  MSScheduler.createJob("SendHeartBeatToManager", "SendHeartBeatToManagerJobGroup", com.osi.agent.jobs.SendHeartBeatToManager.class);
			if(null!=cacheManager){
				sendHeartBeatToManagerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
				}
			sendHeartBeatToManagerTrigger = MSTrigger.createSimpleTrigger("SendHeartBeatToManagerTrigger", "SendHeartBeatToManagerTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_HEARTBEAT_JOB_INTERVAL));
			MSScheduler.scheduleJobWithSimpleTrigger(sendHeartBeatToManagerJob, sendHeartBeatToManagerTrigger);
			LOGGER.info("AgentActivator :: sendHeartBeatToManager()");
			LOGGER.info("AgentActivator :: sendHeartBeatToManager()");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			sendHeartBeatToManagerJob = null;
			sendHeartBeatToManagerTrigger = null;
		}
	}
	private static void getDeviceXmlFromManager() throws SchedulerException{
		JobDetail GetDeviceXmlFromManagerJob=null;
		SimpleTrigger getDeviceXmlFromManagerTrigger=null;
		try {
			 GetDeviceXmlFromManagerJob =  MSScheduler.createJob("GetDeviceXmlFromManager", "SGetDeviceXmlFromManagerJobGroup", com.osi.agent.jobs.GetDeviceXmlFromManager.class);
			if(null!=cacheManager){
				GetDeviceXmlFromManagerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
				}
			 getDeviceXmlFromManagerTrigger = MSTrigger.createSimpleTrigger("GetDeviceXmlFromManagerTrigger", "GetDeviceXmlFromManagerTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_DEVICEXML_JOB_INTERVAL));
			MSScheduler.scheduleJobWithSimpleTrigger(GetDeviceXmlFromManagerJob, getDeviceXmlFromManagerTrigger);
			LOGGER.info(" AgentActivator :: getDeviceXmlFromManager : ");
			LOGGER.info(" AgentActivator :: getDeviceXmlFromManager : ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("AgentActivator :: getDeviceXmlFromManager ");
			e.printStackTrace();
		}finally{
			GetDeviceXmlFromManagerJob = null;
			getDeviceXmlFromManagerTrigger = null;
		}
	}
	private static void getTestConnectionInfo() throws SchedulerException{
		JobDetail getTestConnectionInfo=null;
		SimpleTrigger getTestConnectionInfoTrigger=null;
		try {
		 getTestConnectionInfo =  MSScheduler.createJob("getTestConnectionInfo", "getTestConnectionInfoJobGroup", com.osi.agent.jobs.GetTestConnectionInfo.class);
		if(null!=cacheManager){
			getTestConnectionInfo.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		 getTestConnectionInfoTrigger = MSTrigger.createSimpleTrigger("getTestConnectionInfoTrigger", "getTestConnectionInfoTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_TEST_CONNECTION_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(getTestConnectionInfo, getTestConnectionInfoTrigger);
		LOGGER.info(" AgentActivator :: getTestConnectionInfo() : ");
		LOGGER.info(" AgentActivator :: getTestConnectionInfo() : ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			getTestConnectionInfo = null;
			getTestConnectionInfoTrigger = null;
		}
		}
	private static void getAlertConfigXml() throws SchedulerException{
		JobDetail getAlertConfigXmlFromManagerJob =  MSScheduler.createJob("GetAlertConfigXmlFromManagerJob", "GetAlertConfigXmlFromManagerJobGroup", com.osi.agent.jobs.GetAlertConfigXmlFromManagerJob.class);
		if(null!=cacheManager){
			getAlertConfigXmlFromManagerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		SimpleTrigger getAlertConfigXmlFromManagerTrigger = MSTrigger.createSimpleTrigger("GetAlertConfigXmlFromManagerTrigger", "GetAlertConfigXmlFromManagerTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_ALERTCONFIGXML_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(getAlertConfigXmlFromManagerJob, getAlertConfigXmlFromManagerTrigger);
		LOGGER.info("AgentActivator :: getAlertConfigXml() : ");
		LOGGER.info("AgentActivator :: getAlertConfigXml() : ");
	}
	private static void getUnSchedulerXmlFromManager() throws SchedulerException{
		JobDetail getUnSchedulerXmlFromManagerJob =  MSScheduler.createJob("GetAlertConfigXmlFromManagerJob", "GetAlertConfigXmlFromManagerJobGroup", com.osi.agent.jobs.GetUnSchedulerXmlFromManagerJob.class);
		if(null!=cacheManager){
			getUnSchedulerXmlFromManagerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			} 
		SimpleTrigger getUnSchedulerXmlFromManagerTrigger = MSTrigger.createSimpleTrigger("GetUnSchedulerXmlFromManagerTrigger", "GetUnSchedulerXmlFromManagerTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_UNSCHEDULEXML_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(getUnSchedulerXmlFromManagerJob, getUnSchedulerXmlFromManagerTrigger);
		LOGGER.info("AgentActivator :: getUnSchedulerXmlFromManager() ");
		LOGGER.info("AgentActivator :: getUnSchedulerXmlFromManager() ");
	}
	private static void scheduleMonitoringJobs(){
		AgentStartupProcessor agentStartupProcessor = new AgentStartupProcessor();
		agentStartupProcessor.scheduleMonitoringJobs(cacheManager);
		LOGGER.info("AgentActivator :: scheduleMonitoringJobs(): ");
		LOGGER.info("AgentActivator :: scheduleMonitoringJobs(): ");
	}

	private static void scheduleDeviceConfigJob() throws SchedulerException{
		JobDetail deviceConfigJob=null;
		SimpleTrigger deviceConfigTrigger=null;
		try {
		 deviceConfigJob =  MSScheduler.createJob("DeviceConfigJob", "DeviceConfigJobGroup", com.osi.agent.jobs.DeviceConfigJob.class);
		if(null!=cacheManager){
			deviceConfigJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		 deviceConfigTrigger = MSTrigger.createSimpleTrigger("DeviceConfigTrigger", "DeviceConfigTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_CONFIG_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(deviceConfigJob, deviceConfigTrigger);
		LOGGER.info("AgentActivator ::  scheduleDeviceConfigJob()");
		LOGGER.info("AgentActivator ::  scheduleDeviceConfigJob()");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			deviceConfigJob = null;
			deviceConfigTrigger = null;
		}
	}

	private static void scheduleCheckpointResultsSerializerJob() throws SchedulerException{
		JobDetail checkpointResultsSerializerJob=null;
		SimpleTrigger checkpointResultsSerializerTrigger=null;
		try {
		 checkpointResultsSerializerJob =  MSScheduler.createJob("CheckpointResultsSerializerJob", "CheckpointResultsSerializerJobGroup", com.osi.agent.jobs.CheckpointResultsSerializerJob.class);
		if(null!=cacheManager){
			checkpointResultsSerializerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		 checkpointResultsSerializerTrigger = MSTrigger.createSimpleTrigger("CheckpointResultsSerializerTrigger", "CheckpointResultsSerializerTriggerGroup", Integer.parseInt(KeyValue.MS_RESULT_SERIALIZE_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(checkpointResultsSerializerJob, checkpointResultsSerializerTrigger);
		LOGGER.info("AgentActivator ::  scheduleCheckpointResultsSerializerJob() : ");
		LOGGER.info("AgentActivator ::  scheduleCheckpointResultsSerializerJob() : ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			checkpointResultsSerializerJob = null;
			checkpointResultsSerializerTrigger = null;
		}
		}

	private static void scheduleErrorMonitoringJob() throws SchedulerException{
		JobDetail errorMonitoringJob=null;
		SimpleTrigger errorMonitoringTrigger=null;
		try {
		 errorMonitoringJob =  MSScheduler.createJob("ErrorMonitoringJob", "ErrorMonitoringJobGroup", com.osi.agent.jobs.ErrorMonitoringJob.class);
		if(null!=cacheManager){
			errorMonitoringJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		 errorMonitoringTrigger = MSTrigger.createSimpleTrigger("ErrorMonitoringTrigger", "ErrorMonitoringTriggerGroup", Integer.parseInt(KeyValue.MS_ERROR_MONITORING_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(errorMonitoringJob, errorMonitoringTrigger);
		LOGGER.info("AgentActivator ::  scheduleErrorMonitoringJob() : ");
		LOGGER.info("AgentActivator ::  scheduleErrorMonitoringJob() : ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			errorMonitoringJob = null;
			errorMonitoringTrigger = null;
		}
	}
	
	private static void sendResultToManager() throws SchedulerException{
		JobDetail sendResultToManagerJob=null;
		SimpleTrigger sendResultToManagerJobTrigger=null;
		try {
		 sendResultToManagerJob =  MSScheduler.createJob("SendResultToManagerJob", "SendResultToManagerJobGroup", com.osi.agent.jobs.SendResultToManagerJob.class);
		if(null!=cacheManager){
			sendResultToManagerJob.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
			}
		 sendResultToManagerJobTrigger = MSTrigger.createSimpleTrigger("SendResultToManagerTrigger", "SendResultToManagerTriggerGroup", Integer.parseInt(KeyValue.MS_PARSE_SENDRESULT_TO_MANAGER_JOB_INTERVAL));
		MSScheduler.scheduleJobWithSimpleTrigger(sendResultToManagerJob, sendResultToManagerJobTrigger);
		LOGGER.info("AgentActivator ::  sendResultToManager() : ");
		LOGGER.info("AgentActivator ::  sendResultToManager() : ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("in catchhhhhhhhhhhhh");
			e.printStackTrace();
		}finally{
			sendResultToManagerJob = null;
			sendResultToManagerJobTrigger = null;
		}
	}
}
