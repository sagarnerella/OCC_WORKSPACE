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

public class UnschedulePublisherJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(UnschedulePublisherJob.class);
	private static boolean flag=true;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("UnschedulePublisherJob fired....");
		MSAgent msAgent = null;
		IManagerDao iManagerDao = null;
		SNMPManagerSender managerSender = null;
		List<Device> deviceList=null;
		String detectedOS=null;
		String msconfigPath=null;
		try {
			if(flag){
				flag=false;
				msAgent = (MSAgent) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("GET_AGENTS"));
				//managerSender=(SNMPManagerSender)context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("SNMP_MANAGER_SENDER"));
				if(null != msAgent && !msAgent.equals("")){
					iManagerDao = (IManagerDao) new ManagerDao();
					deviceList = iManagerDao.getDevicesToUnschedule(msAgent);
					if(deviceList != null && deviceList.size() > 0){
						String deviceUnscheduleXML = null;
						for (int index=0;index<deviceList.size();index++) {
							deviceUnscheduleXML = CommonUtilities.getXMLFromObject(deviceList.get(index), Device.class);
							LOGGER.info("devicePublishXML in UnschedulePublisherJob==>"+deviceUnscheduleXML);
							// iManagerDao.saveDeviceXml(msAgent.getAgentId(),deviceUnscheduleXML,"DEVICE_UNSCHEDULE");
							
							// save deviceUnscheduleXML in to MS_DEVICE_UNSCHEDULE_CONFIG
							
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
								    LOGGER.info("UnschedulePublisher files  path ::: "+msconfigPath);
								    
								File destLocation=new File(msconfigPath);
								if(!destLocation.isDirectory()){
									destLocation.mkdirs();
								}
								Device device=(Device)CommonUtilities.getObjectFromXML(deviceUnscheduleXML, Device.class);
								//String deviceId = getIdFromContent(content);
								File file = new File(destLocation.getAbsolutePath()+"/"+"Unschedule_Config_"+device.getDeviceID()+".xml");
								file.createNewFile();

								FileWriter fw = new FileWriter(file.getAbsoluteFile());
								bw = new BufferedWriter(fw);
								bw.write(deviceUnscheduleXML);
								iManagerDao.updatePublisherStatus(deviceList.get(index).getPublisherId(),"P","","");
							} catch (Exception e) {
								iManagerDao.updatePublisherStatus(deviceList.get(index).getPublisherId(),"Z",deviceUnscheduleXML,"");
								throw e;
							}finally{
								bw.close();
							}
							
							
						}
					}
				}
				flag=true;
			}
			LOGGER.info("UnschedulePublisherJob :: execute : UnschedulePublisherJob Completed ");
			
		} catch(CreateException ce){
			LOGGER.error("Error occurred while executing UnschedulePublisherJob",ce);
		} catch(UpdateException ue){
			LOGGER.error("Error occurred while executing UnschedulePublisherJob",ue);
		} catch(FinderException fe){
			LOGGER.error("Error occurred while executing UnschedulePublisherJob",fe);
		}catch (DBConnectException e) {
			LOGGER.error("Error occurred while performing DB operations in UnschedulePublisherJob",e);
		} catch (Exception e) {
			LOGGER.error("Error occurred while executing UnschedulePublisherJob",e);
		} finally {
			flag=true;
		}
		
		
	}
}
