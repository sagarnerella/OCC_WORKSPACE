/**
 * 
 */
package com.osi.agent.snmp.adapter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.DeviceConfigManager;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentSNMPRecieverException;
import com.osi.agent.exception.OCCCheckpointResultsException;
import com.osi.agent.results.CheckpointResultsSenderImpl;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.results.ICheckpointResultsSender;
import com.osi.agent.vo.DeviceCheckPointDetails;

/**
 * @author jkorada1
 *
 */
public class SNMPAgentReciever extends SNMPAgentAdapter implements CommandResponder, Runnable{
	
	private static final Logger LOGGER = Logger.getLogger(SNMPAgentReciever.class);
	
	private static final String HOST_NAME= KeyValue.AGENT_HOST_NAME;
	private static final String PORT= KeyValue.AGENT_PORT;
	private static final String COMMUNITY_STRING= KeyValue.AGENT_COMMUNITY_STRING;
	
	private ICheckpointResultsSender resultSender=null;
	private ICacheManager cacheManager=null;
	
	/**
	 * 
	 */
	public SNMPAgentReciever(){
		
	}
	
	/**
	 * 
	 * @param cacheManager
	 * @throws Exception
	 */
	public SNMPAgentReciever(ICacheManager cacheManager){
		this.cacheManager=cacheManager;
		resultSender=new CheckpointResultsSenderImpl(this.cacheManager);
	}
	
	
	@Override
	public void run() {
		try {
			recieve();
		} catch (OCCAgentSNMPRecieverException e) {
			LOGGER.error("",e);
		}
	}
	
	@Override
	public Object recieve() throws OCCAgentSNMPRecieverException {
		try {
			listen(new UdpAddress(InetAddress.getByName(HOST_NAME),Integer.parseInt(PORT)));
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public synchronized void listen(TransportIpAddress address)
			throws IOException {
		AbstractTransportMapping transport;
		if (address instanceof TcpAddress) {
			transport = new DefaultTcpTransportMapping((TcpAddress) address);
		} else {
			transport = new DefaultUdpTransportMapping((UdpAddress) address);
		}

		ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
		MessageDispatcher mDispathcher = new MultiThreadedMessageDispatcher(
				threadPool, new MessageDispatcherImpl());

		// add message processing models
		mDispathcher.addMessageProcessingModel(new MPv1());
		mDispathcher.addMessageProcessingModel(new MPv2c());

		// add all security protocols
		SecurityProtocols.getInstance().addDefaultProtocols();
		SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

		// Create Target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(COMMUNITY_STRING));

		Snmp snmp = new Snmp(mDispathcher, transport);
		snmp.addCommandResponder(this);

		transport.listen();
		LOGGER.info("Listening on " + address);
		try {
			this.wait();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			LOGGER.error("",ex);
		}
	}

	/**
	 * This method will be called whenever a pdu is received on the given port
	 * specified in the listen() method
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void processPdu(CommandResponderEvent cmdRespEvent){
		LOGGER.info("Received PDU...");
		Vector variableBindings=null;
		String detectedOS=null;
		try {
			PDU pdu=cmdRespEvent.getPDU();
			boolean pduFlag = CommonUtilities.isNotNull(pdu);
			if (pduFlag) {
				LOGGER.info(pdu.toString());
				if ((pdu.getType() != PDU.TRAP) &&
					(pdu.getType() != PDU.V1TRAP) &&
					(pdu.getType() != PDU.REPORT) &&
					(pdu.getType() != PDU.RESPONSE)) {
					
					pdu.setErrorIndex(0);
					pdu.setErrorStatus(0);
					pdu.setType(PDU.RESPONSE);
					StatusInformation statusInformation = new StatusInformation();
					StateReference ref = cmdRespEvent.getStateReference();
					try {
						cmdRespEvent.getMessageDispatcher().returnResponsePdu(cmdRespEvent.
							getMessageProcessingModel(),
							cmdRespEvent.getSecurityModel(),
							cmdRespEvent.getSecurityName(),
							cmdRespEvent.getSecurityLevel(),
							pdu,
							cmdRespEvent.getMaxSizeResponsePDU(),
							ref,
							statusInformation);
					}catch (MessageException ex) {
						LOGGER.error(ex);
						LOGGER.error("Error while sending response: "+ex.getMessage());
					}
				}
				variableBindings=pdu.getVariableBindings();
				if(variableBindings!=null && !variableBindings.isEmpty()){
					String oid=pdu.getVariableBindings().get(0).getOid().toString();
					boolean oidFlag = CommonUtilities.isNotNullAndEmpty(oid);
					if(oidFlag && oid.trim().equalsIgnoreCase(KeyValue.AGENT_RESULT_OID)){
						//send the results
						cacheManager.setRequestPolled(true);
						cacheManager.setLastRequestPolledTime(System.currentTimeMillis());
						LOGGER.info("Request for results : "+pdu.getVariableBindings().get(0).toValueString());
						resultSender.sendResults();
						cacheManager.setRequestPolled(false);
					}else if(oidFlag && oid.trim().equalsIgnoreCase(KeyValue.DEVICE_CONFIG_OID)){
						//process the device configuration
						LOGGER.info("Device Configuration : "+pdu.getVariableBindings().get(0).toValueString());
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
						    		DeviceConfigManager.writeToUpdateConfigLocation(KeyValue.MS_CONFIG_UPDATE_LOCATION_WINDOWS, pdu.getVariableBindings().get(0).toValueString());
						    }else if(detectedOS.equals("linux")){
						           DeviceConfigManager.writeToUpdateConfigLocation(KeyValue.MS_CONFIG_UPDATE_LOCATION_LINUX, pdu.getVariableBindings().get(0).toValueString());
					         }
					
					}else if(oidFlag && oid.trim().equalsIgnoreCase(KeyValue.DEVICE_THRESHOLD_CONFIG_OID)){
						//process the threshold device configuration
						LOGGER.info("Device Threshold Configuration : "+pdu.getVariableBindings().get(0).toValueString());
						DeviceCheckPointDetails deviceCheckPointDetails = DeviceConfigManager.getDeviceThresholdConfig(pdu.getVariableBindings().get(0).toValueString());
						DeviceConfigManager.scheduleThresholdChecks(deviceCheckPointDetails, cacheManager);
					}
				}
			}
		} catch (OCCCheckpointResultsException e) {
			LOGGER.error("",e);
		} catch (Exception e) {
			LOGGER.error("",e);
		}finally{
			boolean variableBindngsFlag = CommonUtilities.isNotNull(variableBindings);
			if(variableBindngsFlag){
				variableBindings.clear();
				variableBindings=null;
			}
		}
	}
	
}
