package com.osi.agent;

import java.util.Locale;

import org.apache.log4j.Logger;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.DeviceConfigManager;
import com.osi.agent.common.KeyValue;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.vo.DeviceCheckPointDetails;

public class AgentStartupProcessor {
	private static final Logger LOGGER = Logger.getLogger(AgentStartupProcessor.class);
	public void scheduleMonitoringJobs(ICacheManager cacheManager) {
		LOGGER.info("AgentStartupProcessor::scheduleMonitoringJobs:Start");
		String detectedOS=null;
		DeviceCheckPointDetails deviceCheckPointDetails=null;
		String OS = null;
		try {
			   OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			   LOGGER.info("AgentStartupProcessor :: os environment : "+OS);
			    if (OS.indexOf("win") >= 0) {
			                detectedOS ="windows";
			              } else if (OS.indexOf("nux") >= 0) {
			                detectedOS ="linux";
			              } else {
			                detectedOS ="other";
			        }
			
			    if(detectedOS.equals("windows")){
			          deviceCheckPointDetails = DeviceConfigManager.readConfigLocation(KeyValue.MS_CONFIG_LOCATION_WINDOWS);
			    }else if(detectedOS.equals("linux")){
			    	 deviceCheckPointDetails = DeviceConfigManager.readConfigLocation(KeyValue.MS_CONFIG_LOCATION_LINUX);
			    }
			    
			DeviceConfigManager.scheduleChecks(deviceCheckPointDetails, cacheManager, false);
			
		} catch (Exception e) {
			CommonUtilities.createErrorLogFile(e);
		}
		LOGGER.info("AgentStartupProcessor::scheduleMonitoringJobs:End");
	}
	
}
