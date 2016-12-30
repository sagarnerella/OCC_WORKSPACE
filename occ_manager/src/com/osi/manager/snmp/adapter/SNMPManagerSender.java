/**
 * 
 */
package com.osi.manager.snmp.adapter;

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

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.exception.OCCComunityTargetException;
import com.osi.manager.exception.OCCManagerSNMPSenderException;
import com.osi.manager.exception.OCCPDUCreationException;
import com.osi.manager.exception.OCCSNMPAdapterInitializationException;

/**
 * @author jkorada1
 *
 */
public class SNMPManagerSender extends SNMPManagerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(SNMPManagerSender.class);
	
	/*private static final String HOST_NAME=CommonUtilities.getProperty("AGENT_HOST_NAME");
	private static final String PORT=CommonUtilities.getProperty("AGENT_PORT");*/
	private static final String COMMUNITY_STRING=CommonUtilities.getProperty("MANAGER_COMMUNITY_STRING");
	private static final String RETRY_ATTEMPTS=CommonUtilities.getProperty("MANAGER_RETRY_ATTEMPTS");
	private static final String TIME_OUT=CommonUtilities.getProperty("MANAGER_TIME_OUT");
	
	
	@SuppressWarnings("rawtypes")
	private TransportMapping transportMapping=null;
	private UdpAddress address=null;
	private Integer agentId=null;
	
	private String hostName=null;
	private Integer port=null;
	
	
	private SNMPManagerSender(){
		
	}
	
	/**
	 * 
	 */
	public SNMPManagerSender(String hostName, Integer  port, Integer agentId){
		try {
			this.agentId=agentId;
			this.hostName=hostName;
			this.port=port;
			init();
		} catch (Exception e) {
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
				InetAddress inetAddress=InetAddress.getByName(hostName);
				address=new UdpAddress(inetAddress, port);
			}
			if(transportMapping==null){
				transportMapping = new DefaultUdpTransportMapping();
			}
			LOGGER.info("SNMPManagerSender running on " + address);
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCSNMPAdapterInitializationException("", "Error while initializing Agent Sender", e.getMessage());
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void destroy() throws Exception{
		address=null;
		if(transportMapping!=null){
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
			LOGGER.error("",e);
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
			LOGGER.error("",e);
			throw new OCCPDUCreationException("", "Error while creating the PDU", e.getMessage());
		}
		return pdu;
	}
	
	/* (non-Javadoc)
	 * @see com.osi.agent.snmp.adapter.SNMPAgentAdapter#send()
	 */
	@Override
	public boolean send(String oid, String value) throws OCCManagerSNMPSenderException {
		boolean flag=false;
		IManagerDao managerDao=null;
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
			LOGGER.info("responseEvent .... "+responseEvent);
			LOGGER.info("responseEvent.getResponse() .... "+responseEvent.getResponse());
			managerDao = new ManagerDao();
			if(responseEvent!=null && responseEvent.getResponse()!=null){
				managerDao.saveAgentPollAudit(agentId,"P");
				flag=true;
			}else{
				managerDao.saveAgentPollAudit(agentId,"Z");
				flag=false;
			}
			snmp.close();
		} catch (OCCComunityTargetException e) {
			LOGGER.error("",e);
			flag=false;
			throw new OCCManagerSNMPSenderException("", e.getUserMessage(), e.getMessage());
		} catch (OCCPDUCreationException e) {
			LOGGER.error("",e);
			flag=false;
			throw new OCCManagerSNMPSenderException("", e.getUserMessage(), e.getMessage());
		} catch (Exception e) {
			if(e.getCause().getMessage()!=null && e.getCause().getMessage().contains("Unexpected end of input stream at position")) {
				flag=true;
			} else {
				flag=false;
				throw new OCCManagerSNMPSenderException("", "Error while sending the PDU", e.getMessage());
			}
			LOGGER.error("",e);
		}finally{
			try {
				destroy();
			} catch (Exception e) {
				LOGGER.error("",e);
				throw new OCCManagerSNMPSenderException("", "Error while closing socket", e.getMessage());
			}
		}
		return flag;
	}

}
