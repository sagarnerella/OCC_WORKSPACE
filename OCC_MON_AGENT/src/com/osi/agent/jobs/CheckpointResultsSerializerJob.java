package com.osi.agent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.vo.CheckpointResult;

public class CheckpointResultsSerializerJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(CheckpointResultsSerializerJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("CheckpointResultsSerializerJob Job fired");		
		ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
		List<CheckpointResult> checkPointResultList = null;
		try {
			if(!cacheManager.isRequestPolled() && cacheManager.getLastRequestPolledTime()!=null 
					&& System.currentTimeMillis()-cacheManager.getLastRequestPolledTime().longValue()>=(Long.parseLong(KeyValue.MS_MANAGER_RESULT_POLLING_INTERVAL)*1000)){
				checkPointResultList = new ArrayList<CheckpointResult>(cacheManager.getResultCache());
				boolean checkPointResultListFlag = CommonUtilities.isNotNull(checkPointResultList);
				if(checkPointResultListFlag && !checkPointResultList.isEmpty()){
					boolean flag=CommonUtilities.serializeCheckPointResultFromMemory(checkPointResultList);
					if(flag){
						cacheManager.removeResultsFromCache(checkPointResultList);
					}
				}
			}
		} catch (Exception e) {
			CommonUtilities.createErrorLogFile(e);
		}
	}
}
