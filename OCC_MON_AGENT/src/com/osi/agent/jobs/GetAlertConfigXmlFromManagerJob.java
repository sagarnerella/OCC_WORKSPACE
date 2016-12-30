package com.osi.agent.jobs;

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

public class GetAlertConfigXmlFromManagerJob implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(GetDeviceXmlFromManager.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOGGER.info("GetAlertConfigXmlFromManagerJob :: execute : start ");
		LOGGER.info("GetAlertConfigXmlFromManagerJob :: execute : start ");
		try{
			LOGGER.info("3");
			
			ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			LOGGER.info("3.1");
			String managerHeartBeat = cacheManager.getManagerHeartBeat();
			if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_SUCCESS"))) {
				String urlToGetAlertDeviceXmlsNames = CommonUtilities
						.getProperty("URL_TO_GETDEVICEXML_NAMES_FROM_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID")+"&publishType="+"ALERT_CONFIG";
				
				String alertConfigDeviceXmlNames=CommonUtilities.getDataFromManager(urlToGetAlertDeviceXmlsNames,null,"GetAlertConfigXmlFromManager");
				
					boolean alertConfigDeviceXmlNamesFlag=CommonUtilities.isNotNullAndEmpty(alertConfigDeviceXmlNames);
					if(alertConfigDeviceXmlNamesFlag){
						StringTokenizer strToken = new StringTokenizer(alertConfigDeviceXmlNames,
								"|");
						while (strToken.hasMoreTokens()) {
							String xmlName = "";
							String deviceXml = "";
						xmlName = strToken.nextElement().toString().trim();
						String urlTogetDeviceXml = CommonUtilities.getProperty("URL_TO_GETDEVICEXML_FROM_MANAGER")
								+"?agentId="+CommonUtilities.getProperty("AGENT_ID")+"&deviceXmlName="+xmlName+"&publishType="+"ALERT_CONFIG";
						deviceXml=CommonUtilities.getDataFromManager(urlTogetDeviceXml,null,"GetDeviceXmlFromManager");
						boolean deviceXmlFlag=CommonUtilities.isNotNullAndEmpty(deviceXml);
						if(deviceXmlFlag){
							DeviceCheckPointDetails deviceCheckPointDetails = DeviceConfigManager.getDeviceThresholdConfig(deviceXml);
							DeviceConfigManager.scheduleThresholdChecks(deviceCheckPointDetails, cacheManager);
						}
						}
					}
			}
		}catch (Exception e) {
				LOGGER.error("", e);
			}
		LOGGER.info("GetAlertConfigXmlFromManagerJob :: execute : end ");
		LOGGER.info("GetAlertConfigXmlFromManagerJob :: execute : end ");
		
	}
}
