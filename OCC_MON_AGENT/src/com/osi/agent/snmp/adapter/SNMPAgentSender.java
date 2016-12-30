/**
 * 
 */
package com.osi.agent.snmp.adapter;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentSNMPSenderException;
import com.osi.agent.exception.OCCComunityTargetException;
import com.osi.agent.exception.OCCPDUCreationException;
import com.osi.agent.exception.OCCSNMPAdapterInitializationException;
import com.osi.agent.results.ICacheManager;

/**
 * @author jkorada1
 *
 */
public class SNMPAgentSender extends SNMPAgentAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(SNMPAgentSender.class);
	
	private static final String HOST_NAME=KeyValue.MANAGER_HOST_NAME;
	private static final String PORT=KeyValue.MANAGER_PORT;
	private static final String COMMUNITY_STRING=KeyValue.MANAGER_COMMUNITY_STRING;
	private static final String RETRY_ATTEMPTS=KeyValue.MANAGER_RETRY_ATTEMPTS;
	private static final String TIME_OUT=KeyValue.MANAGER_TIME_OUT;
	
	
	@SuppressWarnings("rawtypes")
	private TransportMapping transportMapping=null;
	private UdpAddress address=null;
	@SuppressWarnings("unused")
	private ICacheManager cacheManager=null;
	
	/**
	 * 
	 */
	public SNMPAgentSender(){
		try {
			init();
		} catch (Exception e) {
			LOGGER.error("Error while initializing Agent Sender",e);
		}
	}
	
	/**
	 * 
	 * @param cacheManager
	 * @throws Exception
	 */
	public SNMPAgentSender(ICacheManager cacheManager){
		this.cacheManager=cacheManager;
		try {
			init();
		} catch (OCCSNMPAdapterInitializationException e) {
			LOGGER.error("",e);
		}
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	private void init() throws OCCSNMPAdapterInitializationException{
		try {
			if(address==null){
				InetAddress inetAddress=InetAddress.getByName(HOST_NAME);
				address=new UdpAddress(inetAddress, Integer.parseInt(PORT));
			}
			if(transportMapping==null){
				transportMapping = new DefaultUdpTransportMapping();
			}
			LOGGER.info("SNMPManagerSender running on " + address);
		} catch (Exception e) {
			throw new OCCSNMPAdapterInitializationException("", "Error while initializing Agent Sender", e.getMessage());
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void destroy() throws IOException{
		address=null;
		boolean transportMappingFlag = CommonUtilities.isNotNull(transportMapping);
		if(transportMappingFlag){
			transportMapping.close();
			transportMapping=null;
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private CommunityTarget getTarget() throws OCCComunityTargetException{
		CommunityTarget cTarget=null;
		try {
			cTarget=new CommunityTarget();
			cTarget.setCommunity(new OctetString(COMMUNITY_STRING));
			cTarget.setVersion(SnmpConstants.version2c);
			cTarget.setAddress(address);
			cTarget.setRetries(Integer.parseInt(RETRY_ATTEMPTS));
			cTarget.setTimeout(Integer.parseInt(TIME_OUT));
		} catch (Exception e) {
			throw new OCCComunityTargetException("", "Error while setting the target for the sender", e.getMessage());
		}
		return cTarget;
	}
	
	private PDU getPDU(String oid, String value) throws OCCPDUCreationException{
		PDU pdu=null;
		try {
			pdu=new PDU();
			pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
			pdu.setType(PDU.INFORM);	
		} catch (Exception e) {
			throw new OCCPDUCreationException("", "Error while creating the PDU", e.getMessage());
		}
		return pdu;
	}
	
	/* (non-Javadoc)
	 * @see com.osi.agent.snmp.adapter.SNMPAgentAdapter#send()
	 */
	@Override
	public boolean send(String oid, String value) throws OCCAgentSNMPSenderException {
		boolean flag=false;
		try {
			init();
			/*Create Target*/
			CommunityTarget cTarget=this.getTarget();

			/*Create PDU for V2*/
			PDU pdu=getPDU(oid,value);

			/*Send the PDU*/
			Snmp snmp = new Snmp(transportMapping);
			snmp.listen();
			ResponseEvent responseEvent=snmp.send(pdu, cTarget);
			boolean responseEventFlag = CommonUtilities.isNotNull(responseEvent);
			if(responseEventFlag && responseEvent.getResponse()!=null){
				LOGGER.info("Sent results successfully...");
				flag=true;
			}else{
				flag=false;
				LOGGER.info("Sending results failed...");
			}
			snmp.close();
		} catch (OCCComunityTargetException e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPSenderException("", e.getUserMessage(), e.getMessage());
		} catch (OCCPDUCreationException e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPSenderException("", e.getUserMessage(), e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPSenderException("", "Error while sending the PDU", e.getMessage());
		}finally{
			try {
				destroy();
			} catch (Exception e) {
				LOGGER.error("",e);
				throw new OCCAgentSNMPSenderException("", "Error while closing socket", e.getMessage());
			}
		}
		return flag;
	}
}
