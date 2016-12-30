/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osi.manager.scheduler;

/**
 *
 * @author psangineni
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;



public class MSScheduler {
	private static final Logger LOGGER = Logger.getLogger(MSScheduler.class);
	private static Scheduler scheduler = null;
	static {
		try {
			Properties prop = new Properties();
			prop.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
			prop.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			prop.setProperty("org.quartz.threadPool.threadCount", OCCSchedulerConstants.QUARTZ_THREAD_COUNT);
			prop.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
			
			SchedulerFactory schedulerFactory = new StdSchedulerFactory(prop); 

			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException se) {
			//LOGGER.error("SchedulerException", se);
			throw new OCCSchedulerException(OCCSchedulerConstants.SCHEDULER_EXCEPTION_MESSAGE);
			//CommonUtilities.createErrorLogFile(se);
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			//CommonUtilities.createErrorLogFile(e);
			throw e;
		}
	}
	
	private MSScheduler(){
		
	}
	
	public static Scheduler getScheduler(){
		return scheduler;
	}
	/**
	 * @param jobName
	 * @param groupName
	 * @param jobClass
	 * @return
	 */
	public static JobDetail createJob(String jobName, String groupName, @SuppressWarnings("rawtypes") Class jobClass) {
		@SuppressWarnings("unchecked")
		JobDetail jobDetail = JobBuilder.newJob(jobClass)
										.withIdentity(jobName, groupName)
										.build();
    	return jobDetail;
    }
	
    
    public static boolean unscheduleJobs(String triggerGroup, String jobGroupName) throws SchedulerException {
        boolean flag = false;
        try {
			List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup)));
			boolean triggerFlag = scheduler.unscheduleJobs(triggerKeys);
			List<JobKey> jobKeys = new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName)));
			boolean jobsFlag = scheduler.deleteJobs(jobKeys);
			if (triggerFlag && jobsFlag) {
				flag = true;
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw e;
			//CommonUtilities.createErrorLogFile(e);
		}
		return flag;
    }
    
    public static boolean unscheduleJob(String triggerName, String triggerGroup,String jobName, String jobGroupName) throws SchedulerException {
        boolean flag = false;
        try {
        	TriggerKey triggerKey = new TriggerKey(triggerName,triggerGroup);
        	JobKey jobKey = new JobKey(jobName, jobGroupName);
			if (scheduler.unscheduleJob(triggerKey) && scheduler.deleteJob(jobKey)) {
				flag = true;
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			//CommonUtilities.createErrorLogFile(e);
			throw e;
		} 
		return flag;
    }
    
   /* public static boolean unscheduleJob(String triggerName, String jobName) throws SchedulerException {
        boolean flag = false;
        try {
        	TriggerKey triggerKey = new TriggerKey(triggerName);
        	JobKey jobKey = new JobKey(jobName);
			if (scheduler.unscheduleJob(triggerKey) && scheduler.deleteJob(jobKey)) {
				flag = true;
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			e.printStackTrace();
		}
        
		return flag;
    }*/
    public static SimpleTrigger createSimpleTrigger(String triggerName, String groupName, int repeatInterval) {
		SimpleTrigger simpletrigger = TriggerBuilder
				.newTrigger()
				.withIdentity(triggerName, groupName)
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(repeatInterval)
				.repeatForever().withMisfireHandlingInstructionFireNow())
				.build();
    	return simpletrigger;
    }
    public static SimpleTrigger createSimpleTriggerWRCount(String triggerName, String groupName, int repeatCount, int repeatInterval) {
		SimpleTrigger simpletrigger = TriggerBuilder
				.newTrigger()
				.withIdentity(triggerName, groupName)
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(repeatInterval)
				.withRepeatCount(repeatCount-1).withMisfireHandlingInstructionFireNow())
				.build();
    	return simpletrigger;
    }
    public static CronTrigger createCronTrigger(String triggerName, String groupName, String cronExpression, Date startDate, Date endDate) {
    	CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, groupName)
    			.startAt(startDate)
    		    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
    		    .endAt(endDate)
    		    .build();
    	return cronTrigger;
    }
    public static Date scheduleJobWithSimpleTrigger(JobDetail job, SimpleTrigger trigger) throws SchedulerException {
        java.util.Date date = null;
    	date = scheduler.scheduleJob(job, trigger);
    	return date;
    }
    public static Date scheduleJobWithCronTrigger(JobDetail job, CronTrigger trigger) throws SchedulerException {
        java.util.Date date = null;
    	date = scheduler.scheduleJob(job, trigger);
    	return date;
    }
}

