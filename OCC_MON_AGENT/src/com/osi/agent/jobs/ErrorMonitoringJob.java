package com.osi.agent.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.domain.AgentError;
import com.osi.agent.domain.AgentErrors;
import com.osi.agent.exception.OCCReadConfigException;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.snmp.adapter.SNMPAgentSender;

public class ErrorMonitoringJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(ErrorMonitoringJob.class);

	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		LOGGER.info("ErrorMonitoringJob fired");
		String detectedOS=null;
		 AgentErrors agentErrors=null;
		try {
			ICacheManager cacheManager = (ICacheManager) context.getJobDetail()
					.getJobDataMap().get("CACHE_MANAGER");
			String managerHeartBeat = cacheManager.getManagerHeartBeat();
				if (managerHeartBeat != null
						&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
								.getProperty("HEART_BEAT_SUCCESS"))) {
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
			                      agentErrors=readErrorConfigLocation(KeyValue.MS_MON_ERROR_LOG_LOCATION_WINDOWS);
					    }else if(detectedOS.equals("linux")){
					    	agentErrors=readErrorConfigLocation(KeyValue.MS_MON_ERROR_LOG_LOCATION_LINUX);
					    }
			
			boolean agentErrorsFlag = CommonUtilities.isNotNull(agentErrors);
			if(agentErrorsFlag && !agentErrors.getErrors().isEmpty()){
				String errorXml=CommonUtilities.getXMLFromObject(agentErrors, AgentErrors.class);
				//agentSender.send(KeyValue.AGENT_ERROR_OID,xml);	
				boolean erroXmlFlagt=CommonUtilities.isNotNullAndEmpty(errorXml);
				if(erroXmlFlagt){
				String urlToSendErrors=CommonUtilities
						.getProperty("URL_TO_SENDERROR_TOMANAGER");
				String outPut = CommonUtilities.getDataFromManager(
						urlToSendErrors, errorXml,"ErrorMonitoringJob");
				}
			}
			}
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}

	private AgentErrors readErrorConfigLocation(String srcFilesLocation) throws OCCReadConfigException {
		AgentErrors errors=null;
		List<AgentError> agentErrors=new ArrayList<AgentError>(0);
		AgentError agentError=null;
		try {
			File folder = new File(srcFilesLocation);
			File[] listOfFiles = folder.listFiles();
			boolean filesListFlag = CommonUtilities.isNotNull(listOfFiles); 
			if(filesListFlag){
				errors=new AgentErrors();
				for (File file : listOfFiles) {
					if (file.isFile()) {
						agentError = processErrorFile(file);
						boolean agentErrorFlag = CommonUtilities.isNotNull(agentError);
						if(agentErrorFlag){
							agentErrors.add(agentError);
						}
					}
				}
				errors.setErrors(agentErrors);
				boolean folderFlag = CommonUtilities.isNotNull(folder);
				if(folderFlag && folder.isDirectory()){
					FileUtils.cleanDirectory(folder);
				} 
			}
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCReadConfigException("", "Error occured while reading the files in Error Config Location", e.getMessage());
		}
		return errors;

	} 

	private AgentError processErrorFile(File file) throws IOException{
		AgentError agentError=null;
		String message = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		String fileName = file.getName();
		Long timeOfError = Long.parseLong(fileName.substring(0,fileName.length()-4));
		boolean messageFlag = CommonUtilities.isNotNullAndEmpty(message);
		if(messageFlag){
			agentError=new AgentError();
			agentError.setMessage(message);
			agentError.setAgentId(Integer.parseInt(KeyValue.AGENT_ID));
			agentError.setTimeOfError(timeOfError);
		}
		return agentError;
	}	
}
