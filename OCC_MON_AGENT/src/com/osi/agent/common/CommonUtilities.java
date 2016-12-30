package com.osi.agent.common;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.osi.agent.exception.OCCAgentDeserializeException;
import com.osi.agent.exception.OCCAgentSerializeException;
import com.osi.agent.exception.OCCObjectFormatException;
import com.osi.agent.exception.OCCXMLFormatException;
import com.osi.agent.vo.CheckpointResult;
public class CommonUtilities {

	private static final Logger LOGGER = Logger.getLogger(CommonUtilities.class);
	private static Properties msAgentProperties = new Properties();
	static {
		try {
			/*
			 * load the properties file onto Properties object. The properties
			 * file should be in the same location where jar is placed
			 */
			msAgentProperties.load(new FileInputStream(System.getProperty("user.dir")+ "/MSAgent.properties"));
		} catch (IOException e) {
			LOGGER.error("CommonUtilities::readProperties:IOException", e);
		} catch (Exception e) {
			createErrorLogFile(e);
		}
	}
	
	private CommonUtilities(){
		
	}

	public static String getProperty(String key) {
		return msAgentProperties.getProperty(key);
	}


	public static void createErrorLogFile(Exception exception) {
		FileOutputStream fop= null;
		String errorLogFile=null;
		String detectedOS=null;
		String errorLogFolder=null;
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
				    errorLogFolder = msAgentProperties.getProperty("MS_MON_ERROR_LOG_LOCATION_WINDOWS");
		 	        errorLogFile = msAgentProperties.getProperty("MS_MON_ERROR_LOG_LOCATION_WINDOWS")+File.separator+System.currentTimeMillis()+".txt";
		      }else if(detectedOS.equals("linux")){
		    	    errorLogFolder = msAgentProperties.getProperty("MS_MON_ERROR_LOG_LOCATION_LINUX");
			        errorLogFile = msAgentProperties.getProperty("MS_MON_ERROR_LOG_LOCATION_LINUX")+File.separator+System.currentTimeMillis()+".txt";
		     }
			
			File errorLogFolderPath=new File(errorLogFolder);
			if(!errorLogFolderPath.isDirectory()){
				errorLogFolderPath.mkdirs();
			}
			File file = new File(errorLogFile);
			fop = new FileOutputStream(file);
			file.createNewFile();
			StringWriter errors = new StringWriter();
			exception.printStackTrace(new PrintWriter(errors));
			// get the content in bytes
			byte[] contentInBytes = errors.toString().getBytes();
			fop.write(contentInBytes);
		} catch (FileNotFoundException fne) {
			LOGGER.error("CommonUtilities::createErrorLogFile", fne);			
		} catch (IOException ioe) {			
			LOGGER.error("CommonUtilities::createErrorLogFile", ioe);			
		} finally {
			try {
				fop.flush();
				fop.close();
			} catch (IOException e) {
				LOGGER.error("CommonUtilities::createErrorLogFile",e);
			}
		}
	}

	public static File getSerializedDirectory(){
		File serializedDir = null;
	     String detectedOS=null;
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
		               serializedDir = new File(getProperty("MS_MON_RESULT_LOCATION_WINDOWS"));
			    }else if(detectedOS.equals("linux")){
			    	   serializedDir = new File(getProperty("MS_MON_RESULT_LOCATION_LINUX"));
			    }
			    LOGGER.info("CommonUtilities :: getSerializedDirectory : serializedDirPath "+serializedDir);
			    LOGGER.info("CommonUtilities :: getSerializedDirectory : serializedDirPath "+serializedDir);
		if (!serializedDir.exists()) {
			serializedDir.mkdirs();
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}
		return serializedDir;   
 }

	public static boolean serializeCheckPointResultFromMemory(List<CheckpointResult> checkPointResultList) throws OCCAgentSerializeException{
		boolean saveStatus = false;
		if(checkPointResultList != null && !checkPointResultList.isEmpty()){
			ObjectOutput out=null;
			try {
				File directory = getSerializedDirectory();
				out = new ObjectOutputStream(new FileOutputStream(directory+"//"+System.currentTimeMillis()+".ser"));
				out.writeObject(checkPointResultList);
				saveStatus =  true;
			} catch (FileNotFoundException e) {
				LOGGER.error("",e);
				throw new OCCAgentSerializeException("", "Error occured while finding the file", e.getMessage());
			} catch (Exception e) {
				LOGGER.error("",e);
				throw new OCCAgentSerializeException("", "Error occured while serializing the Object", e.getMessage());
			}finally{
				try {
					boolean flag = isNotNull(out);
					if(flag){
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					LOGGER.error("",e);
					throw new OCCAgentSerializeException("", "Error occured while closing the stearm object", e.getMessage());
				}
			}
		}
		return saveStatus;
	}

	@SuppressWarnings("unchecked")
	public static List<CheckpointResult> deserializeCheckpointResultsFromFile() throws OCCAgentDeserializeException {
		List<CheckpointResult> checkPointResultList = new ArrayList<CheckpointResult>(0);
		FileInputStream fileIn=null;
		ObjectInputStream in=null;
		File directory=null;
		File[] listOfFiles=null;
		try{
			directory = getSerializedDirectory();
			listOfFiles = directory.listFiles();
			int i = 1;
			boolean flag = isNotNull(listOfFiles);
			if(flag){
				for (File file : listOfFiles) {
					fileIn = new FileInputStream(file);
					in = new ObjectInputStream(fileIn);
					boolean inStreamFlag = isNotNull(in);
					if(inStreamFlag){
						checkPointResultList.addAll((List<CheckpointResult>) in.readObject());
						in.close();
						boolean fileInFlag = isNotNull(fileIn);
						if(fileInFlag){
							fileIn.close();
						}
						file.delete();
					}
					if(i == Integer.parseInt(msAgentProperties.getProperty("MS_FILE_COUNT")))
						break;
					i++;
				}
			}
		} catch (EOFException ex) { 
			LOGGER.info("End of file reached.");
		} catch (FileNotFoundException e) {
			LOGGER.error("",e);
			throw new OCCAgentDeserializeException("", "Error occured while finding the file", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCAgentDeserializeException("", "Error occured while deserializing the Object", e.getMessage());
		}finally{
			try {
				boolean inStreamFlag = isNotNull(in);
				if(inStreamFlag){
					in.close();
				}
				boolean fileInFlag = isNotNull(fileIn);
				if(fileInFlag){
					fileIn.close();
				}
				/*if(directory!=null && directory.isDirectory()){
					FileUtils.cleanDirectory(directory);
				}*/
			} catch (IOException e) {
				LOGGER.error("",e);
				throw new OCCAgentDeserializeException("", "Error occured while closing the stearm object", e.getMessage());
			}
		}
		return checkPointResultList;
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
			LOGGER.info("XML :: "+xml+ " XML Size::"+xml.length());
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
			StringReader reader = new StringReader(xml);
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
	
	public static boolean isNotNull(Object object){
		boolean flag = false;
		if(null != object){
			flag = true;
		}
		return flag;
	}
	
	public static boolean isNotNullAndEmpty(String string){
		boolean flag = false;
		if(null != string && !"".equalsIgnoreCase(string)){
			flag = true;
		}
		return flag;
	}
	
	public static <T> boolean isNotNullAndSizeNotZero(List<T> objectList){
		boolean flag = false;
		if(null != objectList && objectList.size() > 0){
			flag = true;
		}
		return flag;
	}
	
	
	public static String getDataFromManager(String sUrl,String sData,String jobName){
		String result="";
		HttpURLConnection conn = null;
		try {
				URL url;
			String managerUrl=sUrl;
			url = new URL(managerUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			boolean sDataFlag=CommonUtilities.isNotNullAndEmpty(sData);
			if(sDataFlag){
			OutputStream os = conn.getOutputStream();
			os.write(sData.getBytes());
			LOGGER.info("Body Data "+sData);
			os.flush();
			}
			if (conn.getResponseCode() != 200) {
			       throw new RuntimeException("Failed : HTTP error code : "
			         + conn.getResponseCode());
			      }else{
			    	  String outPut;
			    	  BufferedReader br = new BufferedReader(new InputStreamReader(
			    		       (conn.getInputStream())));
			    	  //outPut = br.readLine();
			    	  //boolean outPutFlag=CommonUtilities.isNotNullAndEmpty(outPut);
			    	  //if(outPutFlag){
			    		      while ((outPut = br.readLine()) != null) {
			    		       LOGGER.info("Out for Job "+jobName+"  "+outPut);
			    		       result=result+outPut;
			    		      }/*else{
			    		    	  LOGGER.info("Manager Sending Empty Result  for Job "+jobName);
			    		      }*/
			    		      LOGGER.info("result "+result);
			      }
		} catch (MalformedURLException e) {
			LOGGER.error("No legal protocol could be found, String or the String could not be parsed for Job "+jobName, e);
			// TODO Auto-generated catch block
			result=CommonUtilities
					.getProperty("HEART_BEAT_FAILURE");;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Unable to Reach the Manager for Job "+jobName, e);
			result=CommonUtilities
					.getProperty("HEART_BEAT_FAILURE");
			e.printStackTrace();
		}catch (Exception e) {
			try {
				result=CommonUtilities
						.getProperty("HEART_BEAT_FAILURE");
				if(conn!=null && conn.getResponseCode()==500)
				LOGGER.error("Error while Processing the Data at Manager Side for Job "+jobName,e);
				if(conn!=null && conn.getResponseCode()==204)
					LOGGER.error("Manager is not sending any Response for Job "+jobName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}finally{
			if(conn!=null)
			conn.disconnect();
		}
		return result;
	}
}
