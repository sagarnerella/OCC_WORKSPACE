package com.osi.agent.jobs;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.DeviceConfigManager;
import com.osi.agent.common.KeyValue;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.vo.DeviceCheckPointDetails;

public class DeviceConfigJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(DeviceConfigJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("DeviceConfigJob::execute:Start");
		LOGGER.info("DeviceConfigJob :: execute :: end ");
		String detectedOS=null;
		DeviceCheckPointDetails deviceCheckPointDetails=null;
		try {
			LOGGER.info("DeviceConfigJob fired...");
			ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			   LOGGER.info("os : "+OS);
			   LOGGER.info("os : "+OS);
			    if (OS.indexOf("win") >= 0) {
			                detectedOS ="windows";
			              } else if (OS.indexOf("nux") >= 0) {
			                detectedOS ="linux";
			              } else {
			                detectedOS ="other";
			        }
			    
			    if(detectedOS.equals("windows")){
			           deviceCheckPointDetails = DeviceConfigManager.readUpdateConfigLocation(KeyValue.MS_CONFIG_UPDATE_LOCATION_WINDOWS);
			    }else if(detectedOS.equals("linux")){
			    	   deviceCheckPointDetails = DeviceConfigManager.readUpdateConfigLocation(KeyValue.MS_CONFIG_UPDATE_LOCATION_LINUX);
			    }
			DeviceConfigManager.scheduleChecks(deviceCheckPointDetails, cacheManager, true);
			LOGGER.info("DeviceConfigJob::execute:end");
			LOGGER.info("DeviceConfigJob :: execute :: end ");
		} catch (Exception e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		}
		LOGGER.info("DeviceConfigJob::execute:End");
	}
}
