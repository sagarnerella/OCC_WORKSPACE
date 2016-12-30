package com.osi.manager.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.exception.OCCManagerSNMPSenderException;
import com.osi.manager.snmp.adapter.SNMPManagerSender;

public class ResultProcessorJob implements Job{
	
	private static final Logger LOGGER = Logger.getLogger(ResultProcessorJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SNMPManagerSender snmpManagerSender=null;
		LOGGER.info("ResultProcessing Job fired...");
		
		/*try {
			snmpManagerSender=(SNMPManagerSender)context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"));
			snmpManagerSender.send(CommonUtilities.getProperty("AGENT_RESULT_OID"),"Request For Results");
		} catch(OCCManagerSNMPSenderException osse){
			LOGGER.error("Error occurred while executing ResultProcessorJob",osse);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing ResultProcessorJob",e);
		}*/
	}
}
