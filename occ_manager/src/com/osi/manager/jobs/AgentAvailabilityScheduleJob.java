package com.osi.manager.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SimpleTrigger;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.config.constants.MSConstants;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.DBConnectException;
import com.osi.manager.exception.FinderException;
import com.osi.manager.exception.PersistException;
import com.osi.manager.exception.UpdateException;
import com.osi.manager.scheduler.MSScheduler;
import com.osi.manager.util.MSConnectionManager;

public class AgentAvailabilityScheduleJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(AgentAvailabilityScheduleJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		/*String jobGroupName="";
		String triggerGroupName="";*/
		@SuppressWarnings("unused")
		int agentId = (int) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_AGENT_ID"));
		
		
		//LOGGER.info("AgentAvailabilityScheduleJob :: execute : AgentAvailabilityScheduleJob fired ...."+agentId);	
		LOGGER.info("AgentAvailabilityScheduleJob :: execute : AgentAvailabilityScheduleJob fired ########################### "+agentId);	
	
		
		try{
			
			boolean unschedulestatus=MSScheduler.unscheduleJobs(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_GROUP_BASE_NAME")+agentId,CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId);
			LOGGER.info("AgentAvailabilityScheduleJob :: execute : unschedulestatusJob fired ::::::: "+ CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId);	
		//	LOGGER.info("AgentAvailabilityScheduleJob :: execute : unschedulestatusJob fired :::::: "+CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId);
			LOGGER.info("AgentAvailabilityScheduleJob :: execute : unschedulestatusJob fired status:::::: "+unschedulestatus);
			
			JobDetail jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_BASE_NAME")+agentId, CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId, com.osi.manager.jobs.AgentAvailabilityStatusJob.class);
            jobDetail.getJobDataMap().put(CommonUtilities.getProperty("AGENT_ID"), agentId);
            jobDetail.getJobDataMap().put(CommonUtilities.getProperty("REPEAT_AGENT_COUNT"), 0);
			jobDetail.getJobDataMap().put(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId, CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME")+agentId);
			SimpleTrigger simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_BASE_NAME")+agentId, CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_GROUP_BASE_NAME")+agentId, Integer.parseInt(CommonUtilities.getProperty("REPEATINTERVAL_AGENT_AVAILABLITY")));
			jobDetail.getJobDataMap().put(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_GROUP_BASE_NAME")+agentId, CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_GROUP_BASE_NAME")+agentId);
			MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
			
		
		}catch(ObjectAlreadyExistsException e){
			LOGGER.error("AgentAvailabilityStatusJob :: execute : got ObjectAlreadyExistsException : ");
			e.printStackTrace();
		}
			catch (Exception e) {
		
			LOGGER.error("AgentAvailabilityStatusJob :: execute : got exception : ");
			e.printStackTrace();
			} 
	 }
}

