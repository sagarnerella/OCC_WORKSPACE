package com.osi.agent.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.osi.agent.domain.Checkpoint;
import com.osi.agent.domain.Device;
import com.osi.agent.domain.JDBC;
import com.osi.agent.domain.JMX;
import com.osi.agent.domain.LogMiner;
import com.osi.agent.domain.SNMP;
import com.osi.agent.domain.SnmpTable;
import com.osi.agent.domain.SNMPWalk;
import com.osi.agent.domain.SSHWrapper;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.DeviceCheckPointDetails;
import com.osi.agent.exception.OCCSchedulerException;
import com.osi.agent.scheduler.MSScheduler;
import com.osi.agent.scheduler.MSTrigger;

public class DeviceConfigManager {
	private static final Logger LOGGER = Logger.getLogger(DeviceConfigManager.class);
	
	private DeviceConfigManager(){	
		
	}
	
	public static DeviceCheckPointDetails readConfigLocation(String srcFilesLocation) {
		DeviceCheckPointDetails deviceCheckPointDetails = new DeviceCheckPointDetails();
		List<CheckPointDetails> checkPointDetails = null;
		List<Integer> deviceIds = null;
		try {
			File folder = new File(srcFilesLocation);
			File[] listOfFiles = folder.listFiles();
			boolean filesListFlag = CommonUtilities.isNotNull(listOfFiles);
			if(filesListFlag){
				checkPointDetails = new ArrayList<CheckPointDetails>(0);
				deviceIds = new ArrayList<Integer>(0);
				for (File file : listOfFiles) {
					boolean flag = checkIsXmlFile(file);
					if (flag) {
						List<CheckPointDetails> childCheckPointDetails = processDeviceXMLFile(file);
						boolean chckPointDetailsFlag = CommonUtilities.isNotNullAndSizeNotZero(childCheckPointDetails);
						if (chckPointDetailsFlag) {
							deviceIds.add(childCheckPointDetails.get(0).getDeviceID());
							checkPointDetails.addAll(childCheckPointDetails);
						}
					}
				}
			}else{
				LOGGER.info("DeviceConfigManager :: readConfigLocation() unable to find files to process in folder:"+srcFilesLocation);
			}
			deviceCheckPointDetails.setCheckPointDetails(checkPointDetails);
			deviceCheckPointDetails.setDeviceIds(deviceIds);
		} catch (Exception e) {
			CommonUtilities.createErrorLogFile(e);
			LOGGER.error("",e);
		}
		return deviceCheckPointDetails;
	}

	public static DeviceCheckPointDetails readUpdateConfigLocation(String srcFilesLocation) {
		DeviceCheckPointDetails deviceCheckPointDetails = new DeviceCheckPointDetails();
		List<CheckPointDetails> checkPointDetails = null;
		List<Integer> deviceIds = null;
		try {
			File folder = new File(srcFilesLocation);
			File[] listOfFiles = folder.listFiles();
			boolean filesListFlag = CommonUtilities.isNotNull(listOfFiles);
			if(filesListFlag){
				checkPointDetails = new ArrayList<CheckPointDetails>(0);
				deviceIds = new ArrayList<Integer>(0);
				for (File file : listOfFiles) {
					boolean flag = checkIsXmlFile(file);
					if (flag) {
						List<CheckPointDetails> childCheckPointDetails = processDeviceXMLFile(file);
						boolean chckPointDetailsFlag = CommonUtilities.isNotNullAndSizeNotZero(childCheckPointDetails);
						if (chckPointDetailsFlag) {
							deviceIds.add(childCheckPointDetails.get(0).getDeviceID());
							checkPointDetails.addAll(childCheckPointDetails);
							copyOrReplace(file);
						}
					}
				}
			}else{
				LOGGER.info("DeviceConfigManager :: readConfigLocation() unable to find files to process in folder:"+srcFilesLocation);
			}
			deviceCheckPointDetails.setCheckPointDetails(checkPointDetails);
			deviceCheckPointDetails.setDeviceIds(deviceIds);
		} catch (Exception e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		}
		return deviceCheckPointDetails;
	}

	private static boolean checkIsXmlFile(File file){
		boolean flag = false;
		if (file.isFile() && file.getName().substring(file.getName().lastIndexOf('.') + 1).equalsIgnoreCase("xml")){
			flag = true;
		}
		return flag;	
	}

	private static List<CheckPointDetails> processDeviceXMLFile(File file) throws Exception {
		List<CheckPointDetails> inncerCheckPointDetails = null;
		String detectedOS=null;
		File configFile=null;
		String OS=null;
		try {
			JAXBContext context = JAXBContext.newInstance(Device.class);
			Unmarshaller un = context.createUnmarshaller();
			Device device = (Device) un.unmarshal(file);
			boolean deviceFlag = CommonUtilities.isNotNull(device);
			if (deviceFlag && device.getDeviceID() !=0) {
				inncerCheckPointDetails = FormingCheckPointDetailsFromDevice(device);
				boolean checkpointsFlag = CommonUtilities.isNotNullAndSizeNotZero(inncerCheckPointDetails);
				if (!checkpointsFlag) {
					MSScheduler.unscheduleJobs(KeyValue.CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+device.getDeviceID(), KeyValue.CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+device.getDeviceID());
					LOGGER.info("Unscheduled with Trigger Group --> "+KeyValue.CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+device.getDeviceID()+" And JobGroup -->"+KeyValue.CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+device.getDeviceID());
					file.delete();
					
					   OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
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
					 configFile = new File(KeyValue.MS_CONFIG_LOCATION_WINDOWS+File.separator+"Device_"+device.getDeviceID()+".xml");
			}else if(detectedOS.equals("linux")){		
				    configFile = new File(KeyValue.MS_CONFIG_LOCATION_LINUX+File.separator+"Device_"+device.getDeviceID()+".xml");
			}
			 
			 configFile.delete();
				}
			} else {
				throw new Exception(KeyValue.ERROR_MESSAGE);
			}
		} catch (JAXBException e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		} catch (Exception e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		}
		return inncerCheckPointDetails;
	}

	private static List<CheckPointDetails> FormingCheckPointDetailsFromDevice(Device device) {
		List<CheckPointDetails> inncerCheckPointDetails = new ArrayList<CheckPointDetails>(0);
		CheckPointDetails checkPointDetails = null;
		
		if(null!=device.getJdbc() && CommonUtilities.isNotNull(device.getJdbc().getCheckpoint())){
			JDBC jdbc = device.getJdbc();
			for (Checkpoint checkpoint : device.getJdbc().getCheckpoint()) {
				checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
				checkPointDetails.setPassword(jdbc.getPassword());
				checkPointDetails.setUserName(jdbc.getUserName());
				checkPointDetails.setDbURL(jdbc.getDbURL());
				checkPointDetails.setDriverClass(jdbc.getDriverClass());
				checkPointDetails.setJdbcConnectType(jdbc.getConnectType());
			
				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		if(null!=device.getJmx() && CommonUtilities.isNotNull(device.getJmx().getCheckpoint())){
			JMX jmx = device.getJmx();
		    for (Checkpoint checkpoint : device.getJmx().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint); 
				checkPointDetails.setJmxConnectType(jmx.getConnectType());
				checkPointDetails.setJmxHostName(jmx.getHostName());
				checkPointDetails.setJmxIpAddress(jmx.getIpAddress());
				checkPointDetails.setJmxPort(jmx.getPort());
			
				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		if(null!=device.getSnmp() && CommonUtilities.isNotNull(device.getSnmp().getCheckpoint())){
		    SNMP snmp = device.getSnmp();
		    for (Checkpoint checkpoint : device.getSnmp().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
				checkPointDetails.setCommunityString(snmp.getCommunityString());
				checkPointDetails.setSnmpConnectType(snmp.getConnectType());
				checkPointDetails.setSnmpIpAddress(snmp.getIpAddress());
				checkPointDetails.setSnmpHostName(snmp.getHostName());
				checkPointDetails.setSnmpPort(snmp.getPort());
				checkPointDetails.setSnmpVersion(snmp.getSnmpVersion());

				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		if(null!=device.getSnmpWalk() && CommonUtilities.isNotNull(device.getSnmpWalk().getCheckpoint())){
		    SNMPWalk snmpWalkp = device.getSnmpWalk();
		    for (Checkpoint checkpoint : device.getSnmpWalk().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
				checkPointDetails.setCommunityString(snmpWalkp.getCommunityString());
				checkPointDetails.setSnmpConnectType(snmpWalkp.getConnectType());
				checkPointDetails.setSnmpIpAddress(snmpWalkp.getIpAddress());
				checkPointDetails.setSnmpHostName(snmpWalkp.getHostName());
				checkPointDetails.setSnmpPort(snmpWalkp.getPort());
				checkPointDetails.setSnmpVersion(snmpWalkp.getSnmpVersion());

				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		if(null!=device.getLogMiner() && CommonUtilities.isNotNull(device.getLogMiner().getCheckpoint())){
			LogMiner fileMonitor = device.getLogMiner();
		    for (Checkpoint checkpoint : device.getLogMiner().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
				checkPointDetails.setFileConnectType(fileMonitor.getConnectType());
				checkPointDetails.setFilehHostName(fileMonitor.getHostName());
				checkPointDetails.setFileIpAddress(fileMonitor.getIpAddress());
				checkPointDetails.setFileUserName(fileMonitor.getUserName());
				checkPointDetails.setFilePassword(fileMonitor.getPassword());
				checkPointDetails.setFilePort(fileMonitor.getPort());
				checkPointDetails.setFileLocation(checkpoint.getFileLocation());
				//checkPointDetails.setIdentifierFieldSeparator(checkpoint.getIdentifierFieldSeparator());
				checkPointDetails.setIncludeParams(checkpoint.getIncludeParams());
				checkPointDetails.setExcludeParams(checkpoint.getExcludeParams());
				//checkPointDetails.setIdentifierFieldsToConsider(checkpoint.getIdentifierFieldsToConsider());
				checkPointDetails.setFileRegExpression(checkpoint.getIndentifierRegexPattern());
			
				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		if(null!=device.getSsh() && CommonUtilities.isNotNull(device.getSsh().getCheckpoint())){
			SSHWrapper sshWrapper = device.getSsh();
		    for (Checkpoint checkpoint : device.getSsh().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
		    	checkPointDetails.setSshConnectType(sshWrapper.getConnectType());
				checkPointDetails.setSshHostName(sshWrapper.getHostName());
				checkPointDetails.setSshIpAddress(sshWrapper.getIpAddress());
				checkPointDetails.setSshUserName(sshWrapper.getUserName());
				checkPointDetails.setSshPassword(sshWrapper.getPassword());
				checkPointDetails.setSshPort(sshWrapper.getPort());
				
				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		
		//Start:Added for SNMPTable
		if(null!=device.getSnmpTable() && CommonUtilities.isNotNull(device.getSnmpTable().getCheckpoint())){
			SnmpTable snmpTable = device.getSnmpTable();
		    for (Checkpoint checkpoint : device.getSnmpTable().getCheckpoint()) {
		    	checkPointDetails = FormingCheckPointDetailsFromDeviceAndCheckpoint(device,checkpoint);
				checkPointDetails.setCommunityString(snmpTable.getCommunityString());
				checkPointDetails.setSnmpTableConnectType(snmpTable.getConnectType()); 
				checkPointDetails.setSnmpTableIpAddress(snmpTable.getIpAddress());
				checkPointDetails.setSnmpTableHost(snmpTable.getHostName());
				checkPointDetails.setSnmpTablePort(snmpTable.getPort());
				checkPointDetails.setSnmpVersion(snmpTable.getSnmpVersion());

				inncerCheckPointDetails.add(checkPointDetails);
			} 
		}
		//End:Added for SNMPTable
		return inncerCheckPointDetails;
	}

	private static CheckPointDetails FormingCheckPointDetailsFromDeviceAndCheckpoint(Device device, Checkpoint checkpoint) {
		CheckPointDetails checkPointDetails = new CheckPointDetails();
		
		checkPointDetails.setCheckPoint(checkpoint.getCheckpoint());
		checkPointDetails.setCheckpointID(checkpoint.getCheckpointID());
		checkPointDetails.setExecutionType(checkpoint.getExecutionType());
		checkPointDetails.setFrequency(checkpoint.getFrequency());
		checkPointDetails.setFromDate(checkpoint.getFromDate());
		checkPointDetails.setRepeatCount(checkpoint.getRepeatCount());
		checkPointDetails.setToDate(checkpoint.getToDate());
		checkPointDetails.setAlternateThreshold(checkpoint.getAlternateThreshold());
		checkPointDetails.setSupressIncident(checkpoint.getSupressIncident());
		checkPointDetails.setSnmpWalkAlgorithm(checkpoint.getSnmpWalkAlgorithm());
		//Start:Added for SNMPTable
		checkPointDetails.setSnmpTableMibName(checkpoint.getMibName());
		checkPointDetails.setSnmpTableAlgorithm(checkpoint.getSnmpTableAlgorithm());
		checkPointDetails.setSnmpTableColumnNames(checkpoint.getSnmpTableColumnNames());
		checkPointDetails.setUnitOfMeasure(checkpoint.getUnitOfMeasure());
		checkPointDetails.setSnmpTableRowId(checkpoint.getSnmpTableRowId());
		//End:Added for SNMPTable
		checkPointDetails.setDeviceID(device.getDeviceID());
		checkPointDetails.setEndDate(device.getEndDate());
		checkPointDetails.setEndTime(device.getEndTime());
		checkPointDetails.setFromDayOfWeek(device.getFromDayOfWeek());
		checkPointDetails.setStartTime(device.getStartTime());
		checkPointDetails.setStartDate(device.getStartDate());
		checkPointDetails.setToDayOfWeek(device.getToDayOfWeek());
		boolean checkTypeFlag = CommonUtilities.isNotNullAndEmpty(checkpoint.getCheckType());
		if (checkTypeFlag){
			checkPointDetails.setCheckType(checkpoint.getCheckType().charAt(0));
		}
		return checkPointDetails;
	}

	private static void copyOrReplace(File source) throws Exception {
		String detectedOS=null;
		File destFile=null;
		File desFolderPath=null;
		 LOGGER.info("copyOrReplace in to MS_CONFIG_LOCATION");
		   LOGGER.info("copyOrReplace in to MS_CONFIG_LOCATION");
		try {
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
			    	desFolderPath=new File(KeyValue.MS_CONFIG_LOCATION_WINDOWS);
			    	destFile = new File(KeyValue.MS_CONFIG_LOCATION_WINDOWS, source.getName());
			    }else if(detectedOS.equals("linux")){
			    	desFolderPath=new File(KeyValue.MS_CONFIG_LOCATION_LINUX);
			    	destFile = new File(KeyValue.MS_CONFIG_LOCATION_LINUX, source.getName());
			    }
			    
			    LOGGER.info("copyOrReplace in to MS_CONFIG_LOCATION ::: desFolderPath "+desFolderPath);
			    LOGGER.info("copyOrReplace in to MS_CONFIG_LOCATION ::: desFolderPath "+desFolderPath);
					if(!desFolderPath.isDirectory()){
						desFolderPath.mkdirs();
					}
			    
			FileUtils.copyFile(source, destFile);
			delete(source);
		} catch (Exception e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		}
	}


	private static void delete(File directory) throws Exception {
		try {
			directory.delete();
		} catch (Exception e) {
			LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		}
	}

	public static void scheduleChecks(DeviceCheckPointDetails deviceCheckPointDetails, ICacheManager cacheManager, boolean unscheduleFlag) {
		try {
			boolean deviceIdsFlag = CommonUtilities.isNotNullAndSizeNotZero(deviceCheckPointDetails.getDeviceIds());
			if (unscheduleFlag && deviceIdsFlag) {
				for (Integer deviceId : deviceCheckPointDetails.getDeviceIds()) {
					MSScheduler.unscheduleJobs(KeyValue.CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+deviceId, KeyValue.CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+deviceId);
					LOGGER.info("Unscheduled and scheduled with Trigger Group --> "+KeyValue.CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+deviceId+" And JobGroup -->"+KeyValue.CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+deviceId);
				}
			}
			boolean checkPointDetailsFlag = CommonUtilities.isNotNullAndSizeNotZero(deviceCheckPointDetails.getCheckPointDetails());
			if (checkPointDetailsFlag) {
				//Added for SNMPTable
				for (CheckPointDetails checkPointDetails : deviceCheckPointDetails.getCheckPointDetails()) {
					JobDetail jobDetail =  MSScheduler.createJob(KeyValue.CHECKPOINT_MONITORING_JOB_BASE_NAME+checkPointDetails.getCheckpointID(), KeyValue.CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+checkPointDetails.getDeviceID(), com.osi.agent.jobs.CheckpointMonitoringJob.class);
					if(null!=cacheManager){
						jobDetail.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
						}
					jobDetail.getJobDataMap().put(KeyValue.CHECKPOINT_DETAILS_MEATADATA, checkPointDetails);
					jobDetail.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
					//Added for SNMPTable
					Date startDate = getFormattedDate(checkPointDetails.getFromDate());
					Date endDate = getFormattedDate(checkPointDetails.getToDate());
					CronTrigger cronTrigger = MSTrigger.createCronTrigger(KeyValue.CHECKPOINT_MONITORING_TRIGGER_BASE_NAME+checkPointDetails.getCheckpointID(), KeyValue.CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+checkPointDetails.getDeviceID(), checkPointDetails.getFrequency(), startDate, endDate);
					if (checkPointDetails.getCheckpointID() != 0 && checkPointDetails.getDeviceID() != 0 && null != checkPointDetails.getFrequency()) {
						//MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
						MSScheduler.scheduleJobWithCronTrigger(jobDetail, cronTrigger);
					} 
					//Added for SNMPTable
				}
			}
		} catch (OCCSchedulerException e) {
			LOGGER.info("AgentActivator.main() : ERROR");
			CommonUtilities.createErrorLogFile(e);
		}catch (SchedulerException e) {
			//LOGGER.error("",e);
			CommonUtilities.createErrorLogFile(e);
		} 
	}

	private static Date getFormattedDate(String date){
		//String dateStr = date+" 00:00:00.0";
		String dateStr = null;
		if(date.length()<11){
			dateStr = date+" 00:00:00.0";
		}else{
			dateStr = date;
		}
		Date formattedDate = null;
		try {
			formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dateStr);
		} catch (ParseException e) {
			CommonUtilities.createErrorLogFile(e);
		}
		return formattedDate;

	}

	public static void scheduleThresholdChecks(DeviceCheckPointDetails deviceCheckPointDetails, ICacheManager cacheManager) {
		try {
			Integer tfrequency = null;
			if (null != deviceCheckPointDetails.getCheckPointDetails()) {
				for (CheckPointDetails checkPointDetails : deviceCheckPointDetails.getCheckPointDetails()) {
					JobDetail jobDetail =  MSScheduler.createJob(KeyValue.THRESHOLD_CHECKPOINT_MONITORING_JOB_BASE_NAME+checkPointDetails.getCheckpointID(), KeyValue.THRESHOLD_CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME+checkPointDetails.getDeviceID(), com.osi.agent.jobs.CheckpointMonitoringJob.class);
					if(null!=cacheManager){
						jobDetail.getJobDataMap().put(KeyValue.CACHE_MANAGER, cacheManager);
						}
					jobDetail.getJobDataMap().put(KeyValue.CHECKPOINT_DETAILS_MEATADATA, checkPointDetails);
					//jobDetail.getJobDataMap().put(CommonUtilities.getProperty("CACHE_MANAGER"), cacheManager);
					if (null != checkPointDetails.getFrequency())
						tfrequency = Integer.parseInt(checkPointDetails.getFrequency());
					SimpleTrigger simpleTrigger = MSTrigger.createSimpleTriggerWRCount(KeyValue.THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_BASE_NAME+checkPointDetails.getCheckpointID(), KeyValue.THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME+checkPointDetails.getDeviceID(), checkPointDetails.getRepeatCount(), tfrequency);
					if (checkPointDetails.getCheckpointID() != 0 && checkPointDetails.getDeviceID() != 0 && null != tfrequency) {
						MSScheduler.scheduleJobWithSimpleTrigger(jobDetail, simpleTrigger);
					} 
				}
			}
		} catch (SchedulerException e) {
			CommonUtilities.createErrorLogFile(e);
		}
	}

	public static DeviceCheckPointDetails getDeviceThresholdConfig(String xml) throws Exception {
		DeviceCheckPointDetails deviceCheckPointDetails = null;
		try {
			deviceCheckPointDetails = new DeviceCheckPointDetails();
			Device device=(Device)CommonUtilities.getObjectFromXML(xml, Device.class);
			if (null != device && device.getDeviceID() !=0) {
				deviceCheckPointDetails.setCheckPointDetails(FormingCheckPointDetailsFromDevice(device));
			} else {
				throw new Exception(KeyValue.ERROR_MESSAGE);
			}
		} catch (JAXBException e) {
			CommonUtilities.createErrorLogFile(e);
		} catch (Exception e) {
			CommonUtilities.createErrorLogFile(e);
		}
		return deviceCheckPointDetails;
	}

	public static void writeToUpdateConfigLocation(String destFilesLocation, String content) throws Exception{
		BufferedWriter bw=null; 
		try {
			LOGGER.info("writeToUpdateConfigLocation destFilesLocation ************************************** "+destFilesLocation);
			LOGGER.info("writeToUpdateConfigLocation destFilesLocation ************************************** "+destFilesLocation);
		//	LOGGER.info("writeToUpdateConfigLocation destFilesLocation ************************************** "+destFilesLocation);
			LOGGER.info("writeToUpdateConfigLocation destFilesLocation ************************************** "+destFilesLocation);
			File destLocation=new File(destFilesLocation);
			if(!destLocation.isDirectory()){
				destLocation.mkdirs();
			}
			Device device=(Device)CommonUtilities.getObjectFromXML(content, Device.class);
			//String deviceId = getIdFromContent(content);
			LOGGER.info("writeToUpdateConfigLocation destLocation.getAbsolutePath() ************************************** "+destLocation.getAbsolutePath());
		//	LOGGER.info("writeToUpdateConfigLocation destLocation.getAbsolutePath() ************************************** "+destLocation.getAbsolutePath());
		//	LOGGER.info("writeToUpdateConfigLocation destLocation.getAbsolutePath() ************************************** "+destLocation.getAbsolutePath());
			LOGGER.info("writeToUpdateConfigLocation destLocation.getAbsolutePath() ************************************** "+destLocation.getAbsolutePath());
			File file = new File(destLocation.getAbsolutePath()+"/Device_"+device.getDeviceID()+".xml");
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (Exception e) {
			throw e;
		}finally{
			bw.close();
		}

	}


}
