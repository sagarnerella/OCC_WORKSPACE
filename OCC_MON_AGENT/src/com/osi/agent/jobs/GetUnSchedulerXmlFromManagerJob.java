package com.osi.agent.jobs;

import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.DeviceConfigManager;
import com.osi.agent.common.KeyValue;
import com.osi.agent.results.ICacheManager;

public class GetUnSchedulerXmlFromManagerJob implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(GetDeviceXmlFromManager.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String detectedOS=null;
		try{
			LOGGER.info("GetUnSchedulerXmlFromManagerJob :: execute :: start ");
			LOGGER.info("GetUnSchedulerXmlFromManagerJob :: execute :: start ");
			LOGGER.info("execute ");
			ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			LOGGER.info("execute 1");
			String managerHeartBeat = cacheManager.getManagerHeartBeat();
			LOGGER.info("execute1.1 ");
			if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_SUCCESS"))) {
				String urlToGetDeviceXmlsNames = CommonUtilities
						.getProperty("URL_TO_GETDEVICEXML_NAMES_FROM_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID")+"&publishType="+"DEVICE_UNSCHEDULE_CONFIG";
				
				String deviceXmlNames=CommonUtilities.getDataFromManager(urlToGetDeviceXmlsNames,null,"GetUnSchedulerXmlFromManagerJob");
				
					boolean deviceXmlNamesFlag=CommonUtilities.isNotNullAndEmpty(deviceXmlNames);
					if(deviceXmlNamesFlag){
						StringTokenizer strToken = new StringTokenizer(deviceXmlNames,
								"|");
						while (strToken.hasMoreTokens()) {
							String xmlName = "";
							String deviceXml = "";
						xmlName = strToken.nextElement().toString().trim();
						String urlTogetDeviceXml = CommonUtilities.getProperty("URL_TO_GETDEVICEXML_FROM_MANAGER")
								+"?agentId="+CommonUtilities.getProperty("AGENT_ID")+"&deviceXmlName="+xmlName+"&publishType="+"DEVICE_UNSCHEDULE";
						deviceXml=CommonUtilities.getDataFromManager(urlTogetDeviceXml,null,"GetDeviceXmlFromManager");
						boolean deviceXmlFlag=CommonUtilities.isNotNullAndEmpty(deviceXml);
						if(deviceXmlFlag){
							
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
												DeviceConfigManager
												.writeToUpdateConfigLocation(
														KeyValue.MS_CONFIG_UPDATE_LOCATION_WINDOWS,
														deviceXml);
							    }else if(detectedOS.equals("linux")){
							    	DeviceConfigManager
									.writeToUpdateConfigLocation(
											KeyValue.MS_CONFIG_UPDATE_LOCATION_LINUX,
											deviceXml);
							    }
						}
					}
				}
			}
		}catch (Exception e) {
				LOGGER.error("", e);
			}
		LOGGER.info("GetUnSchedulerXmlFromManagerJob :: execute :: end ");
		LOGGER.info("GetUnSchedulerXmlFromManagerJob :: execute :: end ");
		
	}
}
