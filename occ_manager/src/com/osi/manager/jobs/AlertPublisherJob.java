package com.osi.manager.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.DBConnectException;
import com.osi.manager.exception.FinderException;
import com.osi.manager.exception.UpdateException;
import com.osi.manager.snmp.adapter.SNMPManagerSender;
import com.osi.manager.vo.Device;
import com.osi.manager.vo.MSAgent;

public class AlertPublisherJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(AlertPublisherJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("AlertPublisherJob fired....");
		MSAgent msAgent = null;
		IManagerDao iManagerDao = null;
		String detectedOS=null;
		String msconfigPath=null;
		try {
			LOGGER.info("AlertPublisherJob :: execute : AlertPublisherJob begin ");	
			
			msAgent = (MSAgent) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("GET_AGENTS"));
			
			if(null != msAgent && !msAgent.equals("")){
				iManagerDao = (IManagerDao) new ManagerDao();
				List<Device> deviceList = iManagerDao.getEntriesToPublish(msAgent,"ALERT_CONFIG");
				if(deviceList != null && deviceList.size() > 0){
					String devicePublishXML = null;
					for (int index=0;index<deviceList.size();index++) {
						devicePublishXML = CommonUtilities.getXMLFromObject(deviceList.get(index), Device.class);
						LOGGER.info("devicePublishXML in AlertPublisherJob==>"+devicePublishXML);
					// iManagerDao.saveDeviceXml(msAgent.getAgentId(),devicePublishXML,"ALERT_CONFIG");
						
						// save devicePublishXML into 
						BufferedWriter bw=null; 
						try {
							
							String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
							   LOGGER.info("os : "+OS);
							    if (OS.indexOf("win") >= 0) {
							                detectedOS ="windows";
							              } else if (OS.indexOf("nux") >= 0) {
							                detectedOS ="linux";
							              } else {
							                detectedOS ="other";
							        }
							    
							    if(detectedOS.equals("windows")){
							    	msconfigPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
							    	msconfigPath=msconfigPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_WINDOWS")+"_"+msAgent.getAgentId();
								}else if(detectedOS.equals("linux")){
									msconfigPath=CommonUtilities.getProperty("MS_CONFIG_LOCATION_LINUX");
									msconfigPath=msconfigPath+CommonUtilities.getProperty("MS_AGENTID_FOLDER_PATH_LINUX")+"_"+msAgent.getAgentId();
									   }
							    LOGGER.info("AlertPublisher files  path ::: "+msconfigPath);
							
						  File destLocation=new File(msconfigPath);
							if(!destLocation.isDirectory()){
								destLocation.mkdirs();
							}
							Device device=(Device)CommonUtilities.getObjectFromXML(devicePublishXML, Device.class);
							//String deviceId = getIdFromContent(content);
							File file = new File(destLocation.getAbsolutePath()+"/"+"Alert_Config_"+device.getDeviceID()+".xml");
							file.createNewFile();

							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							bw = new BufferedWriter(fw);
							bw.write(devicePublishXML);
							iManagerDao.updatePublisherStatus(deviceList.get(index).getPublisherId(),"P","","");
						} catch (Exception e) {
							iManagerDao.updatePublisherStatus(deviceList.get(index).getPublisherId(),"Z",devicePublishXML,"");
							throw e;
						}finally{
							bw.close();
						}
						
						
					}
				}
			}
			
			LOGGER.info("AlertPublisherJob :: execute : AlertPublisherJob Completed ");
			
		} catch(CreateException ce){
			LOGGER.error("Error occurred while executing AlertPublisherJob",ce);
		} catch(UpdateException ue){
			LOGGER.error("Error occurred while executing AlertPublisherJob",ue);
		} catch(FinderException fe){
			LOGGER.error("Error occurred while executing AlertPublisherJob",fe);
		}catch (DBConnectException e) {
			LOGGER.error("Error occurred while performing DB operations in AlertPublisherJob",e);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing AlertPublisherJob",e);
		}
	}
}
