package com.osi.manager.common;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.osi.manager.domain.AgentError;
import com.osi.manager.domain.DeviceCheckpointResult;
import com.osi.manager.exception.OCCManagerDeserializeException;
import com.osi.manager.exception.OCCManagerSerializeException;
import com.osi.manager.exception.OCCObjectFormatException;
import com.osi.manager.exception.OCCXMLFormatException;

public class CommonUtilities {
	private static final Logger LOGGER = Logger.getLogger(CommonUtilities.class);
	private static Properties msManagerProperties = new Properties();
	static {
		try {
			/*
			 * load the properties file onto Properties object. The properties
			 * file should be in the same location where jar is placed
			 */
			//msManagerProperties.load(new FileInputStream(System.getProperty("user.dir")+ "/MSManager.properties"));
			InputStream is = null;
	            is = new CommonUtilities().getClass().getResourceAsStream("/MSManager.properties");
	            msManagerProperties.load(is);
	        
		} catch (FileNotFoundException e) {
			LOGGER.error("CommonUtilities::readProperties:Exception", e);
        } catch (IOException e) {
        	LOGGER.error("CommonUtilities::readProperties:Exception", e);
        } catch (Exception e) {
			LOGGER.error("CommonUtilities::readProperties:Exception", e);
		}
	}

	public static String getProperty(String key) {
		return msManagerProperties.getProperty(key);
	}
	
	public static <T> String getXMLFromObject(Object object, Class<T> clazz) throws OCCXMLFormatException {
		String xml=null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter writer=new StringWriter();
			marshaller.marshal(object, writer);
			xml=writer.toString();
			LOGGER.info("XML : "+xml);
		} catch (JAXBException e) {
        	LOGGER.error("",e);
            throw new OCCXMLFormatException("", "Error occured while converting XML to Object", e.getMessage());
        } catch (Exception e) {
        	LOGGER.error("",e);
        	throw new OCCXMLFormatException("", "Error occured while converting XML to Object", e.getMessage());
        }
		return xml;
	}
	
	public static <T> Object getObjectFromXML(String xml, Class<T> clazz) throws OCCObjectFormatException {
		Object object=null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller un = context.createUnmarshaller();
			StringReader reader=new StringReader(xml);
			object = un.unmarshal(reader);
		} catch (JAXBException e) {
        	LOGGER.error("",e);
            throw new OCCObjectFormatException("", "Error occured while converting XML to Object", e.getMessage());
        } catch (Exception e) {
        	LOGGER.error("",e);
        	throw new OCCObjectFormatException("", "Error occured while converting XML to Object", e.getMessage());
        }
		return object;
	}
	
	public static File getSerializedDirectory(String oid){
		File serializedDir = null;
		String detectedOS=null;
		
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
				    if(oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_RESULT_OID")))
						 serializedDir = new File(getProperty("MS_MANAGER_RESULT_LOCATION_WINDOWS"));
						 else if(oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_ERROR_OID")))
							 serializedDir = new File(getProperty("MS_MANAGER_AGENTERROR_RESULT_LOCATION_WINDOWS")); 
		    }else if(detectedOS.equals("linux")){
		        	  if(oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_RESULT_OID")))
							 serializedDir = new File(getProperty("MS_MANAGER_RESULT_LOCATION_LINUX"));
							 else if(oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_ERROR_OID")))
								 serializedDir = new File(getProperty("MS_MANAGER_AGENTERROR_RESULT_LOCATION_LINUX")); 
		     }
		
		if (!serializedDir.exists()) {
			serializedDir.mkdirs();
		}
		return serializedDir;   
	}
	
	
	public static <T> boolean serializeDataFromMemory(List<T> resultList,String oid) throws OCCManagerSerializeException{
		boolean saveStatus = false;
		
		if(resultList != null && !resultList.isEmpty()){
			ObjectOutput out=null;
			try {
				File directory = getSerializedDirectory(oid);
				out = new ObjectOutputStream(new FileOutputStream(directory+"//"+System.currentTimeMillis()+".ser"));
				out.writeObject(resultList);
				saveStatus =  true;
			} catch (FileNotFoundException e) {
				LOGGER.error("",e);
				throw new OCCManagerSerializeException("", "Error occured while finding the file", e.getMessage());
			} catch (Exception e) {
				LOGGER.error("",e);
				throw new OCCManagerSerializeException("", "Error occured while serializing the Object", e.getMessage());
			}finally{
				try {
					if(out!=null){
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					LOGGER.error("",e);
					throw new OCCManagerSerializeException("", "Error occured while closing the stearm object", e.getMessage());
				}
			}
		}
		return saveStatus;
	}
	
	
	public static List<DeviceCheckpointResult> deserializeCheckpointResultsFromFile(String oid) throws OCCManagerDeserializeException {
		List<DeviceCheckpointResult> resultList= new ArrayList<DeviceCheckpointResult>(0);
		FileInputStream fileIn=null;
		ObjectInputStream in=null;
		File directory=null;
		File[] listOfFiles=null;
		try{
			directory = getSerializedDirectory(oid);
			listOfFiles = directory.listFiles();
			int i = 1;
			if(listOfFiles != null){
				for (File file : listOfFiles) {
					fileIn = new FileInputStream(file);
					in = new ObjectInputStream(fileIn);
					if(in!=null){
						resultList.addAll((List<DeviceCheckpointResult>) in.readObject());
						in.close();
						if(fileIn!=null){
							fileIn.close();
						}
						file.delete();
					}
					if(i == Integer.parseInt(getProperty("MS_FILE_COUNT")))
	                    break;
	                i++;
				}
			}
		} catch (EOFException ex) { 
		 /*This exception will be caught when EOF is reached*/
			LOGGER.info("End of file reached.");
        } catch (FileNotFoundException e) {
			LOGGER.error("",e);
			throw new OCCManagerDeserializeException("", "Error occured while finding the file", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCManagerDeserializeException("", "Error occured while deserializing the Object", e.getMessage());
		}finally{
			try {
				if(in!=null){
					in.close();
				}
				if(fileIn!=null){
					fileIn.close();
				}
				/*if(directory!=null && directory.isDirectory()){
					FileUtils.cleanDirectory(directory);
				}*/
			} catch (IOException e) {
				LOGGER.error("",e);
				throw new OCCManagerDeserializeException("", "Error occured while closing the stearm object", e.getMessage());
			}
		}
		return resultList;
	}
	
	public static List<AgentError> deserializeAgentErrors(String oid) throws OCCManagerDeserializeException {
		List<AgentError> resultList= new ArrayList<AgentError>(0);
		FileInputStream fileIn=null;
		ObjectInputStream in=null;
		File directory=null;
		File[] listOfFiles=null;
		try{
			directory = getSerializedDirectory(oid);
			listOfFiles = directory.listFiles();
			int i = 1;
			if(listOfFiles != null){
				for (File file : listOfFiles) {
					fileIn = new FileInputStream(file);
					in = new ObjectInputStream(fileIn);
					if(in!=null){
						resultList.addAll((List<AgentError>) in.readObject());
						in.close();
						if(fileIn!=null){
							fileIn.close();
						}
						file.delete();
					}
					if(i == Integer.parseInt(getProperty("MS_FILE_COUNT")))
	                    break;
	                i++;
				}
			}
		} catch (EOFException ex) { 
		 /*This exception will be caught when EOF is reached*/
			LOGGER.info("End of file reached.");
        } catch (FileNotFoundException e) {
			LOGGER.error("",e);
			throw new OCCManagerDeserializeException("", "Error occured while finding the file", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCManagerDeserializeException("", "Error occured while deserializing the Object", e.getMessage());
		}finally{
			try {
				if(in!=null){
					in.close();
				}
				if(fileIn!=null){
					fileIn.close();
				}
				/*if(directory!=null && directory.isDirectory()){
					FileUtils.cleanDirectory(directory);
				}*/
			} catch (IOException e) {
				LOGGER.error("",e);
				throw new OCCManagerDeserializeException("", "Error occured while closing the stearm object", e.getMessage());
			}
		}
		return resultList;
	}
	
}
