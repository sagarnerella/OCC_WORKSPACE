package com.osi.agent.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.results.ICacheManager;

public class SendHeartBeatToManager implements Job {
	private static final Logger LOGGER = Logger.getLogger(SendHeartBeatToManager.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("SendHeartBeatToManager::execute:Start");
		LOGGER.info("SendHeartBeatToManager :: execute :: start ");
		ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
		String managerUrl=CommonUtilities.getProperty("HEART_BEAT_URL_OF_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID");
		String heartBeat=CommonUtilities.getDataFromManager(managerUrl,null,"SendHeartBeatToManager");
		cacheManager.setManagerHeartBeat(heartBeat);
		cacheManager.setManagerHeartBeat(CommonUtilities.getProperty("HEART_BEAT_SUCCESS"));
		LOGGER.info("SendHeartBeatToManager :: execute :: end ");
		LOGGER.info("SendHeartBeatToManager :: execute :: end ");
			
}
}
