package com.osi.agent.jobs;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.webservice.CheckConnectionImpl;
import com.osi.agent.webservice.CheckConnectionIntf;

public class GetTestConnectionInfo implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(GetTestConnectionInfo.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOGGER.info("GetTestConnectionInfo :: execute :: start ");
		LOGGER.info("GetTestConnectionInfo :: execute :: start ");
			ICacheManager cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			String managerHeartBeat = cacheManager.getManagerHeartBeat();
			/*if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_SUCCESS"))) {*/
				String urlToGetTestConnectionInfo = CommonUtilities
						.getProperty("URL_TO_GET_TEST_CONNECTIONINFO_FROM_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID");
				
				String testConnectionInfo=CommonUtilities.getDataFromManager(urlToGetTestConnectionInfo,null,"GetTestConnectionInfo");
				    StringTokenizer strToken = new StringTokenizer(testConnectionInfo,
							"&");
				    String ipAddress="";
				    String tableOid="";
				    String mib="";
				    String communityStrg="";
					while (strToken.hasMoreTokens()) {
						String  strConfigDet = strToken.nextElement().toString().trim();
						LOGGER.info("strConfigDet "+strConfigDet);
						String strConfigDetArr[]=strConfigDet.split("=");
						
						if(strConfigDetArr[0].equals("address"))
							ipAddress=strConfigDetArr[1];
						if(strConfigDetArr[0].equals("tableoid"))
							tableOid=strConfigDetArr[1];
						if(strConfigDetArr[0].equals("mib"))
							mib=strConfigDetArr[1];
						if(strConfigDetArr[0].equals("communitystrg"))
							communityStrg=strConfigDetArr[1];
					}
				CheckConnectionIntf checkConnectionImpl =new CheckConnectionImpl();
				String result=checkConnectionImpl.executeSnmpTable(ipAddress, tableOid, mib, communityStrg);		
				
				String urlSendTestConnectionResult = CommonUtilities
						.getProperty("URL_TO_SEND_TEST_CONNECTIONRESULT_TO_MANAGER")+"?agentId="+CommonUtilities.getProperty("AGENT_ID");
				
				CommonUtilities.getDataFromManager(urlSendTestConnectionResult,result,"GetTestConnectionInfo");
				//}
				LOGGER.info("GetTestConnectionInfo :: execute :: end ");
				LOGGER.info("GetTestConnectionInfo :: execute :: end ");
	}
}
