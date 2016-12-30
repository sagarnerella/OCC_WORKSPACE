/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osi.manager.dao;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.domain.AgentError;
import com.osi.manager.domain.AgentErrors;
import com.osi.manager.exception.OCCObjectFormatException;
import com.osi.manager.snmp.adapter.SNMPManagerReciever;
import com.osi.manager.snmp.adapter.SNMPManagerSender;
import com.osi.manager.vo.DeviceXml;
import com.osi.manager.vo.MSAgent;

import java.util.Locale;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

import com.osi.manager.scheduler.MSScheduler;
import com.osi.manager.jobs.AlertPublisherJob;
import com.osi.manager.jobs.ResultProcessorJob;
import com.osi.manager.jobs.UnschedulePublisherJob;
/**
 *
 * @author psangineni
 */
public class JobService {
    
    private static final Logger LOGGER = Logger.getLogger(JobService.class);
    
    
  public String  scheduleJobRegistration(String jobname) {
		IManagerDao iManagerDao = null;
		try {
			iManagerDao = (IManagerDao)new ManagerDao();
			List<MSAgent> msAgentsList = iManagerDao.getAgentsList();
			SNMPManagerSender managerSender=null;
			SNMPManagerReciever managerReciever=null;
			for (MSAgent msAgent : msAgentsList) {
				LOGGER.info("Scheduled Jobs for Agent :: ---> "+msAgent.getAgentId());
				LOGGER.info(" Schedule Jobs for Agent :: "+msAgent.getAgentId());
				//managerSender=new SNMPManagerSender(msAgent.getIpAddress(), msAgent.getPort(),msAgent.getAgentId());
				if(msAgent.getIpAddress().equalsIgnoreCase(CommonUtilities.getProperty("MANAGER_HOST_NAME"))){
					//managerReciever=new SNMPManagerReciever(CommonUtilities.getProperty("MANAGER_HOST_NAME"),Integer.parseInt(CommonUtilities.getProperty("MANAGER_PORT")));
				}else{
					//managerReciever=new SNMPManagerReciever(CommonUtilities.getProperty("MANAGER_HOST_NAME"), msAgent.getPort());
				}
				//Device Config Publisher Job
				JobDetail jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), com.osi.manager.jobs.ConfigPublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"), managerSender);
				SimpleTrigger simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Alert publisher job
				jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("THRESHOLD_PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), AlertPublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"), managerSender);
				simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("THRESHOLD_PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("THRESHOLD_PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Unschedule publisher job
				jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), UnschedulePublisherJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("GET_AGENTS"), msAgent);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"), managerSender);
				simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("UNSCHEDULE_PUBLISHER_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Result processor job
				jobDetail =  MSScheduler.createJob(CommonUtilities.getProperty("RESULT_PROCESSOR_JOB_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("RESULT_PROCESSOR_JOB_GROUP_BASE_NAME")+msAgent.getDeviceId(), ResultProcessorJob.class);
				jobDetail.getJobDataMap().put(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"), managerSender);
				simpleTrigger = MSScheduler.createSimpleTrigger(CommonUtilities.getProperty("RESULT_PROCESSOR_TRIGGER_BASE_NAME")+msAgent.getAgentId(), CommonUtilities.getProperty("RESULT_PROCESSOR_TRIGGER_GROUP_BASE_NAME")+msAgent.getDeviceId(), Integer.parseInt(CommonUtilities.getProperty("RESULT_PROCESSOR_FREQUENCY")));
				MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
				
				//Start Manager Recievers for each Agent
				//Thread thread=new Thread(managerReciever);
				//thread.start();
				//break;
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while initializing scheduleJobRegistration",e);
		}
  return "1";	
  }  

    public String SendDeviceXml(String jobname,int agentId) {
       IManagerDao iManagerDao = null;
       String result="";
		try {
			iManagerDao = (IManagerDao)new ManagerDao();
			List<DeviceXml> deviceXmlList=iManagerDao.getDeviceXmlList(jobname,agentId);
			boolean statusUpdateFlag=false;
			for(DeviceXml deviceXml:deviceXmlList){
				boolean sendFlag=false;
				LOGGER.info("deviceXml list @ SendDeviceXml "+deviceXml.getDeviceXml());
				result=deviceXml.getDeviceXml();
				/*if(sendFlag){
					statusUpdateFlag=iManagerDao.updateSaveDeviceXml(deviceXml.getId(),deviceXml.getAgentId(),jobname,"P");
				}else{
					statusUpdateFlag=iManagerDao.updateSaveDeviceXml(deviceXml.getId(),deviceXml.getAgentId(),jobname,"Z");
				}*/
				statusUpdateFlag=iManagerDao.updateSaveDeviceXml(deviceXml.getId(),deviceXml.getAgentId(),jobname,"P");
				LOGGER.info("result send successfully..."+statusUpdateFlag);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while SendDeviceXml ",e);
		}
  return result;
    }

    
    public String getAgentHertBeat() {
       
   return "Y";
     }
    
    
    
    public String getAgentHeartBeat(String agentId) {
    	IManagerDao iManagerDao = null;
    	boolean statusflag=false;
    	String heartbeatstatus="failure";
    	try {
	    		iManagerDao = (IManagerDao)new ManagerDao();
				statusflag=iManagerDao.saveAgentHeartBeat(agentId);
				if(statusflag==true){
					heartbeatstatus="success";
				}
    	} catch (Exception e) {
			LOGGER.error("Error occurred while getting  getAgentHeartBeat",e);
		 }
		return heartbeatstatus;
   }
    
    
    public String agentAvailabilityStatus() {
    	IManagerDao iManagerDao = null;
    	boolean statusflag=false;
    	String heartbeatstatus="failure";
    	try {
	    		iManagerDao = (IManagerDao)new ManagerDao();
				statusflag=iManagerDao.agentAvailabilityStatus();
				if(statusflag==true){
					heartbeatstatus="success";
				}
    	} catch (Exception e) {
			LOGGER.error("Error occurred while getting agentAvailabilityStatus",e);
		 }
		return heartbeatstatus;
   }
    
    public String getDeviceXmlList(String agentId) {
    	IManagerDao iManagerDao = null;
    	String deviceXmlList="";
    	
    	try {
	    		iManagerDao = (IManagerDao)new ManagerDao();
	    		deviceXmlList=iManagerDao.getDeviceXmlList(agentId);
				
    	} catch (Exception e) {
			LOGGER.error("Error occurred while getting getDeviceXmlList",e);
		 }
		return deviceXmlList;
   }
    
    public String getDeviceXml(String agentId, String deviceXmlName) {
    	IManagerDao iManagerDao = null;
    	String deviceXml="";
    	try {
	    		iManagerDao = (IManagerDao)new ManagerDao();
	    		deviceXml=iManagerDao.getDeviceXml(agentId, deviceXmlName);
				
    	} catch (Exception e) {
			LOGGER.error("Error occurred while getting getDeviceXml",e);
		 }
    	
    	// implementation to delete devicexml files in device config location
    	if(deviceXml.contains("<")){
    		deletedeviceXmlFiles(agentId, deviceXmlName);
    	}
    	
		return deviceXml;
   }
    
    public String saveErrorResultXml(String errorResultXml){
    	String resultXmlStatus=null;
    	try{
    		LOGGER.info("errorResultXml :::: "+errorResultXml);
    	AgentErrors agentErrors;
		List<AgentError> agentErrorResultList = null;
		IManagerDao managerDao=null;
		boolean agentStatus = false;
		
		agentErrorResultList = new ArrayList<AgentError>(0);
		String errorXml = errorResultXml;
		AgentErrors agentErrorsNew = null;
			agentErrorsNew = (AgentErrors) CommonUtilities.getObjectFromXML(errorXml, AgentErrors.class);
		
		agentErrorResultList.addAll(agentErrorsNew.getErrors());
		agentErrors = new AgentErrors();
		agentErrors.setErrors(agentErrorResultList);
		if(agentErrors.getErrors()!=null && agentErrors.getErrors().size()!=0){
		managerDao = new ManagerDao();
		agentStatus = managerDao.saveAgentErrors(agentErrors);
		agentStatus = true;
		}
		if(agentStatus){
			LOGGER.info("Agent Errors saved successfully");
		    resultXmlStatus="Agent Errors saved successfully";
		}
		else{
			LOGGER.info("Error while saving agent errors");
			resultXmlStatus="Error while saving Agent Errors";
		}
    } catch (OCCObjectFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    return resultXmlStatus;
}
    
   private void deletedeviceXmlFiles(String agentId, String deviceXmlName){
	   String detectedOS=null;
	   String path=null;
	   String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
	   LOGGER.info("os "+OS);
	    if (OS.indexOf("win") >= 0) {
	                detectedOS ="windows";
	              } else if (OS.indexOf("nux") >= 0) {
	                detectedOS ="linux";
	              } else {
	                detectedOS ="other";
	                
	        }
	    
	    if(detectedOS.equals("windows")){
			    path=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
			    path=path+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+agentId+"\\"+deviceXmlName;
		}else if(detectedOS.equals("linux")){
	        	  path=CommonUtilities.getProperty("MS_CONFIG_LOCATION_LINUX");
				  path=path+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_LINUX")+"_"+agentId+"/"+deviceXmlName;
			   }
	       
	           try{
	      		
	      		File file = new File(path);
	          	
	      		if(file.delete()){
	      			LOGGER.info(file.getName() + " file is deleted from path : "+path);
	      		}else{
	      			LOGGER.info(file.getName() + " file delete operation is failed from path : "+path);
	      		}
	      	   
	      	}catch(Exception e){
	      		
	      		e.printStackTrace();
	      		
	      	}
   }
   
   
   public String getCheckinfo(String agentId, String address, String tableOid, String mib, String communityStrg) {
   	IManagerDao iManagerDao = null;
   	String checkstatus="";
   	try {
	    		iManagerDao = (IManagerDao)new ManagerDao();
	    		checkstatus=iManagerDao.getCheckinfo(agentId, address, tableOid, mib, communityStrg);
				
   	} catch (Exception e) {
			LOGGER.error("Error occurred while getting getTestConnectionStatus",e);
		 }
   	
   	// implementation to delete devicexml files in device config location
   	
   	return checkstatus;
  }
   
   public String sendTestConnectionInfo(String agentId) {
	   	IManagerDao iManagerDao = null;
	   	String checkdetails="";
	   	try {
		    		iManagerDao = (IManagerDao)new ManagerDao();
		    		checkdetails=iManagerDao.sendTestConnectionInfo(agentId);
					
	   	} catch (Exception e) {
				LOGGER.error("Error occurred while getting sendTestConnectionStatus",e);
			 }
	   	
	   	// implementation to delete devicexml files in device config location
	   	
	   	return checkdetails;
	  }
   public String getTestConnectionInfoResult(String agentId, String checkresult) {
	   	IManagerDao iManagerDao = null;
	   	String checkdetailsStatus="";
	   	try {
		    		iManagerDao = (IManagerDao)new ManagerDao();
		    		checkdetailsStatus=iManagerDao.getTestConnectionInfoResult(agentId, checkresult);
					
	   	} catch (Exception e) {
				LOGGER.error("Error occurred while getting getTestConnectionStatus",e);
			 }
	   	
	   	// implementation to delete devicexml files in device config location
	   	
	   	return checkdetailsStatus;
	  }
    
}
