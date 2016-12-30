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
import com.osi.agent.vo.DeviceCheckPointDetails;

public class GetDeviceXmlFromManager implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(GetDeviceXmlFromManager.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String detectedOS=null;
		LOGGER.info("GetDeviceXmlFromManager :: execute :: start ");
		LOGGER.info("GetDeviceXmlFromManager :: execute :: start ");
		try{
			ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			String managerHeartBeat = cacheManager.getManagerHeartBeat();
			if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_SUCCESS"))) {
				String urlToGetDeviceXmlsNames = CommonUtilities
						.getProperty("URL_TO_GETDEVICEXML_NAMES_FROM_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID");
				
				String deviceXmlNames=CommonUtilities.getDataFromManager(urlToGetDeviceXmlsNames,null,"GetDeviceXmlFromManager");
				LOGGER.info("deviceXmlNames "+deviceXmlNames);
					boolean deviceXmlNamesFlag=CommonUtilities.isNotNullAndEmpty(deviceXmlNames);
					if(deviceXmlNamesFlag){
						StringTokenizer strToken = new StringTokenizer(deviceXmlNames,
								"|");
						while (strToken.hasMoreTokens()) {
							String xmlName = "";
							String deviceXml = "";
						xmlName = strToken.nextElement().toString().trim();
						LOGGER.info("xmlName "+xmlName);
						boolean xmlNameFlag=CommonUtilities.isNotNullAndEmpty(xmlName);
						if(xmlNameFlag){
						String urlTogetDeviceXml = CommonUtilities.getProperty("URL_TO_GETDEVICEXML_FROM_MANAGER")
								+"?agentId="+CommonUtilities.getProperty("AGENT_ID")+"&deviceXmlName="+xmlName;
						deviceXml=CommonUtilities.getDataFromManager(urlTogetDeviceXml,null,"GetDeviceXmlFromManager");
						boolean deviceXmlFlag=CommonUtilities.isNotNullAndEmpty(deviceXml);
						if(deviceXmlFlag){
							if(xmlName.contains("Device_Config") || xmlName.contains("Unschedule_Config"))
							{
						
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
								    	 LOGGER.info("MS_CONFIG_UPDATE_LOCATION_LINUX :: ");
										   LOGGER.info("MS_CONFIG_UPDATE_LOCATION_LINUX :::::: ");
								    	DeviceConfigManager
										.writeToUpdateConfigLocation(
												KeyValue.MS_CONFIG_UPDATE_LOCATION_LINUX,
												deviceXml);
								    }
						
						
							}else if(xmlName.contains("Alert_Config")){
								DeviceCheckPointDetails deviceCheckPointDetails = DeviceConfigManager.getDeviceThresholdConfig(deviceXml);
								DeviceConfigManager.scheduleThresholdChecks(deviceCheckPointDetails, cacheManager);
							}
						}
						}
						
						}
					}
			}
		}catch (Exception e) {
				LOGGER.error("", e);
			}
		LOGGER.info("GetDeviceXmlFromManager :: execute :: end ");
		LOGGER.info("GetDeviceXmlFromManager :: execute :: end ");
	}
}
