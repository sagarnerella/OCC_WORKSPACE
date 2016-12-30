package com.osi.agent.jmx;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.exception.OCCAgentJMXException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.snmp.SNMPMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class JMXMonitor implements IMonitor {

	private static final Logger LOGGER = Logger.getLogger(SNMPMonitor.class);
	private JMXConnector connector;

	public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentJMXException {

		String jmxHost =checkPointDetails.getJmxIpAddress();
		Integer jmxPort = checkPointDetails.getJmxPort();
        String l_sResultDesc="";
		String objectName=null;
		String subAttributeName=null;
		String attributeName=null;
		CheckpointResult checkPointResult= null;
		String checkPoint= checkPointDetails.getCheckPoint();
		String l_sException=null;
		String attributeString = null;
		String l_sResultDescription=null;
		try{
			boolean checkPointFlag = CommonUtilities.isNotNullAndEmpty(checkPoint);
			if(checkPointFlag){
				if(checkPoint.contains("~")){
					String[] str_arr = checkPoint.split("~");
					if(str_arr.length == 2){
						subAttributeName = str_arr[1];
						attributeString = str_arr[0];
					}
				}else
					attributeString = checkPoint;
			}
			String[] str_arr = attributeString.split("@");
			if(str_arr.length == 2){
				objectName = str_arr[0];
				attributeName = str_arr[1];
			}
			MBeanServerConnection serverConnection=null;

			if(connector==null){
				connector=JMXConnectorFactory.newJMXConnector(createConnectionURL(jmxHost, jmxPort), null);
			}

			try{
				connector.connect();
				serverConnection = connector.getMBeanServerConnection();
			}catch (IOException e) {
				serverConnection = null;
				 l_sException=e.getMessage();
				checkPointResult = new CheckpointResult();
				if(l_sException.contains("ServiceUnavailableException") || l_sException.contains("ConnectException")){
					checkPointResult.setCheckPointresult("Please check for Server mode, whether it is running or not and check for valid IP Address and Port  ");	
					l_sResultDesc="Please check for Server mode, whether it is running or not and check for valid IP Address and Port  ";
				}else{
				checkPointResult.setCheckPointresult("Device Not Connected");
				l_sResultDesc="Device Not Connected";
				}
				checkPointResult.setFormatedValue("-1");
				}
				catch (SecurityException  e) {
					serverConnection = null;
					checkPointResult = new CheckpointResult();
					checkPointResult.setFormatedValue("-1");
					checkPointResult.setCheckPointresult("The connection could not be made for security reasons");
					l_sResultDesc="The connection could not be made for security reasons";
			}

			boolean connectionFlag = CommonUtilities.isNotNull(serverConnection);
			if(connectionFlag){
				checkPointResult = new CheckpointResult();
				ObjectName objectMXBean=new ObjectName(objectName);
				boolean subAttributeNameFlag = CommonUtilities.isNotNullAndEmpty(subAttributeName);
				boolean attributeNameFlag = CommonUtilities.isNotNullAndEmpty(attributeName);
				if (subAttributeNameFlag) {
					if (attributeNameFlag) {
						CompositeDataSupport dataSenders = (CompositeDataSupport) serverConnection.getAttribute(objectMXBean,attributeName);
						checkPointResult.setCheckPointresult(dataSenders.get(subAttributeName).toString());
						checkPointResult.setFormatedValue(dataSenders.get(subAttributeName).toString());
						l_sResultDesc=dataSenders.get(subAttributeName).toString();
						
					}
				}else if (attributeNameFlag) {
					String lresult=(serverConnection.getAttribute(objectMXBean, attributeName)).toString();
					checkPointResult.setCheckPointresult(lresult);
					checkPointResult.setFormatedValue(lresult);
					l_sResultDesc=lresult;
				}
			}
		}catch (MalformedObjectNameException e) {
			LOGGER.error("",e);
			l_sResultDesc="Error while Creating the ObjectName";
			throw new OCCAgentJMXException("", "Error while Creating the ObjectName", e.getMessage());
		}catch (MalformedURLException e) {
			LOGGER.error("",e);
			l_sResultDesc="Error while Creating the JMX connection URL";
			throw new OCCAgentJMXException("", "Error while Creating the JMX connection URL", e.getMessage());
		}catch (AttributeNotFoundException  e) {
			checkPointResult = new CheckpointResult();
			l_sResultDesc="Error in checkpoint configuration , The "+attributeName+" attribute  is not accessible  ";
			checkPointResult.setCheckPointresult(e.getMessage());
			checkPointResult.setFormatedValue("-1");
			LOGGER.error("",e);
			throw new OCCAgentJMXException("", "Error in checkpoint configuration ,The attribute specified is not accessible with "+attributeName, e.getMessage());
		}catch (InstanceNotFoundException  e) {
			checkPointResult = new CheckpointResult();
			String serviceArray[]=objectName.split("=");
			String objctName=null;
			if(serviceArray.length>=2)
				objctName=objectName.split("=")[1];
			else
				objctName=" Empty ";
			l_sResultDesc="Error in checkpoint configuration , "+objctName +" object  is not registered in the  server.";
			checkPointResult.setCheckPointresult(e.getMessage());
			
			checkPointResult.setFormatedValue("-1");
			LOGGER.error("",e);
			throw new OCCAgentJMXException("", "Error in checkpoint configuration , "+objctName+ " object  is not registered in the  server.", e.getMessage());
		}
		catch (Exception e) {	
			l_sException=e.getMessage();	
			checkPointResult = new CheckpointResult();
			if(null!=l_sException && l_sException.contains(attributeName+" name cannot be null")){
				checkPointResult.setCheckPointresult("name cannot be null  ");
				l_sResultDesc="Error in checkpoint configuration , The "+attributeName+" attribute  is not accessible  ";
			}
			else if(null!=l_sException && l_sException.contains("JMImplementation")){
				checkPointResult.setCheckPointresult(l_sException);	
				l_sResultDesc="Service is not available with  "+checkPoint+" check ";
			}
			else if(null!=l_sException && l_sException.contains("Port out of range")){
				checkPointResult.setCheckPointresult(l_sException);
				l_sResultDesc=" Jmx port out of range please check "+jmxPort+" port with jmxremote.port in standalone.conf file ";
			}
			else{
				checkPointResult.setCheckPointresult("Service is not available with  "+checkPoint+" check ");
				l_sResultDesc="Error in checkpoint configuration , The "+checkPoint+"   is not accessible  ";
			}
			checkPointResult.setFormatedValue("-1");
			LOGGER.error("",e);
			l_sResultDesc="Error while getting the agent JMX performCheck";
			throw new OCCAgentJMXException("", "Error while getting the agent JMX performCheck", e.getMessage());
		}finally{
			if(connector!=null){
				try {
					connector.close();
				} catch (IOException e) {
					LOGGER.error("",e);
				}
			}
		}
		checkPointResult.setResultDesc(l_sResultDesc);
		return checkPointResult;
	}

	private JMXServiceURL createConnectionURL(String host, int port) throws MalformedURLException{
		JMXServiceURL serviceURL=new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
		return serviceURL;
	}
}
