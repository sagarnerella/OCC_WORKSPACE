/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osi.manager.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.osi.manager.ManagerService;
import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.JobService;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.domain.DeviceCheckpointResult;
import com.osi.manager.domain.DeviceCheckpointResults;
import com.osi.manager.exception.OCCObjectFormatException;
import com.osi.manager.jobs.AlertPublisherJob;
import com.osi.manager.jobs.ResultProcessorJob;
import com.osi.manager.jobs.UnschedulePublisherJob;
import com.osi.manager.snmp.adapter.SNMPManagerReciever;
import com.osi.manager.snmp.adapter.SNMPManagerSender;
import com.osi.manager.vo.MSAgent;
import com.osi.manager.webservice.CallAgentWebService;
import com.osi.manager.webservice.CallAgentWebServiceInterface;
import com.osi.manager.scheduler.MSScheduler;




import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author psangineni
 */
public class ManagerStartupProcessor  implements ManagerService{
     private static final Logger LOGGER = Logger.getLogger(ManagerStartupProcessor.class);
       @Autowired
	private JobService jobService; 

    @Override
    public Response getAgentHeartBeat(String agentId) {
    	LOGGER.info("ManagerStartupProcessor :: getAgentHeartBeat: start agentId : "+agentId);
    	
    	try{
    	
	    	if(agentId.equals("") || agentId.equals(null))
			{
				return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
					      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
					      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
			}else{
				LOGGER.info(" ManagerStartupProcessor :: getAgentHeartBeat : end agentId : "+agentId);
	              return Response.ok(jobService.getAgentHeartBeat(agentId)).header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
						      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
			}
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
				      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
				      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    	}
    }

    public Response agentAvailabilityStatus() {
    	LOGGER.info(" ManagerStartupProcessor :: agentAvailabilityStatus : start ");
    	
       return Response.ok(jobService.agentAvailabilityStatus()).header("Access-Control-Allow-Origin", "*")
		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
  
    
    
    @Override
    public void ScheduleAgentAvailabilityStatus(){
    	IManagerDao iManagerDao = null;
		try {
			iManagerDao = (IManagerDao)new ManagerDao();
			List<MSAgent> msAgentsList = iManagerDao.getAgentsList();
			
			for (MSAgent msAgent : msAgentsList) {
				LOGGER.info(" ManagerStartupProcessor :: ScheduleAgentAvailabilityStatus for Agent : start : " +msAgent.getAgentId());
				//LOGGER.info(" ManagerStartupProcessor :: ScheduleAgentAvailabilityStatus for Agent : start : " +msAgent.getAgentId());
				int agentId=msAgent.getAgentId();
				
				//ScheduleAgentAvailabilityStatus
				 JobDetail jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_JOB_BASE_NAME")+agentId, CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_JOB_GROUP_BASE_NAME")+agentId, com.osi.manager.jobs.AgentAvailabilityScheduleJob.class);
			     jobDetail.getJobDataMap().put(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_AGENT_ID"), agentId);
			     
				 SimpleTrigger simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_BASE_NAME")+agentId, CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_GROUP_BASE_NAME")+agentId, Integer.parseInt(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_FREQUENCY")));
				 jobDetail.getJobDataMap().put(CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_BASE_NAME"), CommonUtilities.getProperty("AGENTAVAILABILITYSCHEDULE_TRIGGER_GROUP_BASE_NAME")+agentId);
				 MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				 
				 
			}
		} catch (Exception e) {
			e.getMessage();
			LOGGER.error("ManagerStartupProcesso :: Error occurred while scheduling ScheduleAgentAvailabilityStatus ",e);
		}
    }
    
    
    @Override
    public void scheduleConfigPublisherJobs(){
    	IManagerDao iManagerDao = null;
		try {
			iManagerDao = (IManagerDao)new ManagerDao();
			List<MSAgent> msAgentsList = iManagerDao.getAgentsList();
			
			for (MSAgent msAgent : msAgentsList) {
				LOGGER.info(" ManagerStartupProcessor :: Scheduled Jobs for Agent : start : " +msAgent.getAgentId());
				
				JobDetail jobDetail;
				SimpleTrigger simpleTrigger;
				//Device Config Publisher Job
				 jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), com.osi.manager.jobs.ConfigPublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				 simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Alert publisher job
				jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("THRESHOLD_PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), AlertPublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("THRESHOLD_PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Unschedule publisher job
				jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), UnschedulePublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
			
			
			}
		} catch (Exception e) {
			e.getMessage();
			LOGGER.error("ManagerStartupProcessor :: Error occurred while scheduleConfigPublisherJobs ",e);
		}
    }
    
    
    @Override
    public Response getDeviceXmlList(String agentId) {
    	LOGGER.info(" ManagerStartupProcessor :: getDeviceXmlList : start ");
    try{	
    	
	    	if(agentId.equals("") || agentId.equals(null))
			{
				return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
					      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
					      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
			}else{
				LOGGER.info(" ManagerStartupProcessor :: getDeviceXmlList : end ");
	       return Response.ok(jobService.getDeviceXmlList(agentId)).header("Access-Control-Allow-Origin", "*")
			      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
			}
    
    }catch(Exception e){
		e.printStackTrace();
		return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
			      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
    
   }
    
    @Override
    public Response getDeviceXml(String agentId, String deviceXmlName) {
    	LOGGER.info(" ManagerStartupProcessor :: getDeviceXml : start ");
    	
    	try{
		    	if((agentId.equals("") || agentId.equals(null)) || (deviceXmlName.equals("") || deviceXmlName.equals(null)))
				{
					return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
						      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
				}else{
					LOGGER.info(" ManagerStartupProcessor :: getDeviceXml : end ");
				       return Response.ok(jobService.getDeviceXml(agentId, deviceXmlName)).header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
						      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
				}
    
	    }catch(Exception e){
			e.printStackTrace();
			return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
				      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
				      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
		}
    	
    }
    
    @Override
    public String receiveResultXml(String resultXml) {
    	LOGGER.info(" ManagerStartupProcessor ::: receiveResultXml : start ");
    	List<DeviceCheckpointResult> checkPointResultList = null;
    	DeviceCheckpointResults dcpResultListnew = null;
    	IManagerDao managerDao=null;
    	DeviceCheckpointResults dcpResultList=null;
    	boolean status = false;
    	String receivedXmlStatus="failed";
    	//resultXml="<deviceCheckpointResults><checkpointResult checkType='82' checkpointExecutionTime='1467303300000' checkpointId='280' deviceId='99' downtimeScheduled='0' status='78' value='0'/></deviceCheckpointResults>";
    	if(resultXml.equals("") || resultXml.equals(null)){
    		return receivedXmlStatus="Bad request";
    	}else{
    	
		    try {
					dcpResultListnew = (DeviceCheckpointResults) CommonUtilities.getObjectFromXML(resultXml, DeviceCheckpointResults.class); 
			
					checkPointResultList = new ArrayList<DeviceCheckpointResult>(0);
					checkPointResultList.addAll(dcpResultListnew.getCheckpointResult());
					dcpResultList = new DeviceCheckpointResults();
					dcpResultList.setCheckpointResult(checkPointResultList);
					if(dcpResultList.getCheckpointResult()!=null && dcpResultList.getCheckpointResult().size()!=0){
						managerDao = new ManagerDao();
					    status = managerDao.saveMonitoringResult(dcpResultList);
					    //LOGGER.info("status ::: "+status);
					}
				
    	}catch (OCCObjectFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			receivedXmlStatus="Unable to save the resultxml into database";
		}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					receivedXmlStatus="Unable to save the resultxml into database";
				}
				
				if(status==true){
					receivedXmlStatus="success";
				}else{
					receivedXmlStatus="failed";
				}
				LOGGER.info(" ManagerStartupProcessor ::: receiveResultXml : end ");
		return receivedXmlStatus;
    }
 }

	  
	
	@Override
	public String saveErrorDetailes(String errorResultXml){
		LOGGER.info(" ManagerStartupProcessor :: saveErrorDetailes : start");
		String result="";
		try{
				if(errorResultXml.equals("") || errorResultXml.equals(null))
				{
					result="Bad request";
				}else{
					   result=jobService.saveErrorResultXml(errorResultXml);
				}
				LOGGER.info(" ManagerStartupProcessor :: saveErrorDetailes : end ");
		
		}catch(Exception e){
			e.getStackTrace();
			result="Bad request";
		}
		return result;
	}
	
	
	@Override
	public Response getCheckinfo(String agentId, String address, String tableOid, String mib, String communityStrg) {
		/*agentId="54";
		address="192.168.174.152"; 
		tableOid=".1.3.6.1.2.1.25.2.3"; 
		mib="HOST-RESOURCES-MIB"; 
		communityStrg="occ-osi";*/
		
		//address=192.168.24.18&tableOid=.1.3.6.1.2.1.25.2.3&mib=HOST-RESOURCES-MIB&communityStrg=public
		
		LOGGER.info(" ManagerStartupProcessor :: getCheckinfo : start ");
		try{ 
				if((agentId.equals("") || agentId.equals(null)) || (address.equals("") || address.equals(null))  || (tableOid.equals("") || tableOid.equals(null)) || (mib.equals("") || mib.equals(null)) || (communityStrg.equals("") || communityStrg.equals(null)))
				{
					return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
						      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
				}else{
					LOGGER.info(" ManagerStartupProcessor :: getCheckinfo : end ");
				       return Response.ok(jobService.getCheckinfo(agentId, address, tableOid, mib, communityStrg)).header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
						      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
				}
		
		}catch(Exception e){
				LOGGER.info(" ManagerStartupProcessor :: getCheckinfo : got Exception :  ");
				e.printStackTrace();
				return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
					      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
					      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
		}
    }
		
	@Override
	public Response sendTestConnectionInfo(String agentId) {
		/*agentId="54";
		address="192.168.24.18"; 
		tableOid="1.3.6.1.2.1.25.2.3"; 
		mib="HOST-RESOURCES-MIB"; 
		communityStrg="public";*/
		
		LOGGER.info(" ManagerStartupProcessor :: sendTestConnectionStatus : start ");
		try{
				 if(agentId.equals("") || agentId.equals(null))
					{
						return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
							      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
							      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
					}else{
						LOGGER.info(" ManagerStartupProcessor :: sendTestConnectionStatus : end ");
					       return Response.ok(jobService.sendTestConnectionInfo(agentId)).header("Access-Control-Allow-Origin", "*")
							      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
							      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
					}
	 
		}catch(Exception e){
			e.printStackTrace();
			return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
				      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
				      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
		}
    }
	
	
	@Override
	public Response getTestConnectionInfoResult(String agentId, String checkresult) {
		/*agentId="54";
		checkresult="success";
		address="192.168.24.18"; 
		tableOid="1.3.6.1.2.1.25.2.3"; 
		mib="HOST-RESOURCES-MIB"; 
		communityStrg="public";*/
/*		checkresult="[{'value':['hrStorageIndex','hrStorageType','hrStorageDescr','hrStorageAllocationUnits','hrStorageSize','hrStorageUsed','hrStorageAllocationFailures']},{'value':"

+"['1','.iso.org.dod.internet.mgmt.mib-2.host.hrStorage.hrStorageTypes.hrStorageRam','Physical memory','1024','6121320','4903932','']},{'value':"

+"['3','.iso.org.dod.internet.mgmt.mib-2.host.hrStorage.hrStorageTypes.hrStorageVirtualMemory','Virtual memory','1024','12216160','4903932','']},{'value':"

+"['6','.iso.org.dod.internet.mgmt.mib-2.host.hrStorage.hrStorageTypes.hrStorageOther','Memory buffers','1024','6121320','333840','']}]";*/
			//LOGGER.info(" ManagerStartupProcessor :: getTestConnectionInfoResult : ");
			//LOGGER.info("agentId :"+agentId+" checkresult :"+checkresult);
			LOGGER.info(" ManagerStartupProcessor :: getTestConnectionStatus : start ");
			try{
				 if((agentId.equals("") || agentId.equals(null)) || (checkresult.equals("") || checkresult.equals(null)))
					{
						return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
							      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
							      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
					}else{
						LOGGER.info(" ManagerStartupProcessor :: getTestConnectionStatus : end ");
					       return Response.ok(jobService.getTestConnectionInfoResult(agentId, checkresult)).header("Access-Control-Allow-Origin", "*")
							      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
							      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
					}
			
			}catch(Exception e){
				e.printStackTrace();
				return Response.ok("Bad Request").header("Access-Control-Allow-Origin", "*")
					      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
					      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
		}
		 
    }
 
  
}
