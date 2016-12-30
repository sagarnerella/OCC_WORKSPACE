/**
 * 
 */
package com.osi.manager.snmp.adapter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.domain.AgentError;
import com.osi.manager.domain.AgentErrors;
import com.osi.manager.domain.DeviceCheckpointResult;
import com.osi.manager.domain.DeviceCheckpointResults;
import com.osi.manager.exception.OCCManagerSNMPRecieverException;
import com.osi.manager.exception.OCCManagerSerializeException;

/**
 * @author jkorada1
 *
 */
public class SNMPManagerReciever extends SNMPManagerAdapter implements CommandResponder, Runnable{
	
	private static final Logger LOGGER = Logger.getLogger(SNMPManagerReciever.class);
	private static final String COMMUNITY_STRING=CommonUtilities.getProperty("MANAGER_COMMUNITY_STRING");
	private String hostName=null;
	private Integer port=null;
	
	private SNMPManagerReciever() {
		
	}
	
	public SNMPManagerReciever(String hostName, Integer port) {
		this.hostName=hostName;
		this.port=port;
	}
	
	@Override
	public void run() {
		try {
			recieve();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public Object recieve() throws OCCManagerSNMPRecieverException {
		try {
			listen(new UdpAddress(InetAddress.getByName(hostName),port));
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
		}
	}

	/**
	 * This method will be called whenever a pdu is received on the given port
	 * specified in the listen() method
	 */
	public synchronized void processPdu(CommandResponderEvent cmdRespEvent){
		LOGGER.info("Received PDU...");
		List<DeviceCheckpointResult> checkPointResultList = null;
		List<AgentError> agentErrorResultList = null;
		Vector variableBindings=null;
		IManagerDao managerDao=null;
		String oid = null;
		DeviceCheckpointResults dcpResultList;
		AgentErrors agentErrors;
		try {
			PDU pdu=cmdRespEvent.getPDU();
			if (pdu != null) {
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
							System.err.println("Error while sending response: "+ex.getMessage());
						}
					}
				variableBindings=pdu.getVariableBindings();
				if(variableBindings!=null && variableBindings.size()>0){
					oid=pdu.getVariableBindings().get(0).getOid().toString();
					
					if(oid!=null && oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_RESULT_OID"))){
						LOGGER.info("Received Results");
						/*process the check point results*/
						boolean status = false;
						checkPointResultList = new ArrayList<DeviceCheckpointResult>(0);
						checkPointResultList = (List<DeviceCheckpointResult>) CommonUtilities.deserializeCheckpointResultsFromFile(oid);
						
						String resultXml = pdu.getVariableBindings().get(0).toValueString().trim();
						DeviceCheckpointResults dcpResultListnew = (DeviceCheckpointResults) CommonUtilities.getObjectFromXML(resultXml, DeviceCheckpointResults.class);
						checkPointResultList.addAll(dcpResultListnew.getCheckpointResult());
						dcpResultList = new DeviceCheckpointResults();
						dcpResultList.setCheckpointResult(checkPointResultList);
						if(dcpResultList.getCheckpointResult()!=null && dcpResultList.getCheckpointResult().size()!=0){
						managerDao = new ManagerDao();
						status = managerDao.saveMonitoringResult(dcpResultList);
						}
						if(status)
							LOGGER.info("Results processed successfully");
						else{
							LOGGER.info("There are no results to process");
						}
					} else if(oid!=null && oid.trim().equalsIgnoreCase(CommonUtilities.getProperty("AGENT_ERROR_OID"))){
						/*process the errors*/
						boolean agentStatus = false;
						agentErrorResultList = new ArrayList<AgentError>(0);
						agentErrorResultList = CommonUtilities.deserializeAgentErrors(oid);
						String errorXml = pdu.getVariableBindings().get(0).toValueString().trim();
						AgentErrors agentErrorsNew = (AgentErrors) CommonUtilities.getObjectFromXML(errorXml, AgentErrors.class);
						agentErrorResultList.addAll(agentErrorsNew.getErrors());
						agentErrors = new AgentErrors();
						agentErrors.setErrors(agentErrorResultList);
						if(agentErrors.getErrors()!=null && agentErrors.getErrors().size()!=0){
						managerDao = new ManagerDao();
						agentStatus = managerDao.saveAgentErrors(agentErrors);
						agentStatus = true;
						}
						if(agentStatus)
							LOGGER.info("Agent Errors saved successfully");
						else{
							LOGGER.info("Error while saving agent errors");
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("",e);
			try {
				if(((checkPointResultList!=null && checkPointResultList.size() != 0) || (agentErrorResultList!=null && agentErrorResultList.size() != 0) ) && oid!=null){
					if(checkPointResultList!=null && checkPointResultList.size() != 0)
						CommonUtilities.serializeDataFromMemory(checkPointResultList,oid);
					else if(agentErrorResultList!=null && agentErrorResultList.size() != 0)
						CommonUtilities.serializeDataFromMemory(agentErrorResultList,oid);
				}
			} catch (OCCManagerSerializeException e1) {
				LOGGER.error(e1);
			}	
		}
	}
}
