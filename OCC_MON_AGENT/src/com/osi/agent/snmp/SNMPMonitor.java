package com.osi.agent.snmp;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentException;
import com.osi.agent.exception.OCCAgentSNMPException;
import com.osi.agent.exception.OCCComunityTargetException;
import com.osi.agent.exception.OCCPDUCreationException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class SNMPMonitor implements IMonitor {

	private static final Logger LOGGER = Logger.getLogger(SNMPMonitor.class);
	private static final String COMMUNITY_STRING=KeyValue.MANAGER_COMMUNITY_STRING;
	private Snmp snmp = null;
	private String address = null;
	private CheckPointDetails checkPointDetail = null;
	@SuppressWarnings("rawtypes")
	private TransportMapping transportMapping=null;
	private ResponseEvent responseEvent = null;
	String result=null;
	String resultDesc="";
	String formatedValue="";
	@Override
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentSNMPException {
		LOGGER.info("SNMPMonitor Job start...");
		CheckpointResult checkpointResult = null;
		try {
			if(checkPointDetails.isValid(checkPointDetails)){	
				checkPointDetail = checkPointDetails;
				boolean checkPointDetailFlag = CommonUtilities.isNotNull(checkPointDetail);
				if(checkPointDetailFlag){
				address = checkPointDetail.getSnmpIpAddress().trim()+"/"+checkPointDetail.getSnmpPort();
				start();
				result = getResponseEvnet(new OID(checkPointDetail.getCheckPoint().trim()));
				if(result!=null)
				formatedValue=result;
				else
					formatedValue="-1";
			}
			}else{
				result="Error While SNMP Monitoring, due to checkPointDetails are not valid ";
				resultDesc="Error While SNMP Monitoring, due to checkPointDetails are not valid";
				formatedValue="-1";
				}
			
		} catch (OCCPDUCreationException e) {
			//com.osi.agent.exception.OCCPDUCreationException: For input string: "v2"
			LOGGER.error("",e);
			//throw new OCCAgentSNMPException("", e.getUserMessage(), e.getMessage());
		} catch (OCCAgentException e) {
			LOGGER.error("",e);
			//throw new OCCAgentSNMPException("", e.getUserMessage(), e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			//throw new OCCAgentSNMPException("", "Error while getting the agent performCheck", e.getMessage());
		} finally{
				checkpointResult = new CheckpointResult();
				checkpointResult.setCheckPointresult(result);
				checkpointResult.setResultDesc(resultDesc);
				checkpointResult.setFormatedValue(formatedValue);
				checkpointResult.setCheckPointDetails(checkPointDetails);
			destroy();
		}
		LOGGER.info("SNMPMonitor Job end...");
		return checkpointResult;
	}
	
	private void start() throws Exception {
		try{
			transportMapping = new DefaultUdpTransportMapping();
			snmp = new Snmp(transportMapping);
			transportMapping.listen();
		}catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private void destroy() {
		try{
			boolean snmpFlag = CommonUtilities.isNotNull(snmp);
			if (snmpFlag) {
				snmp.close();
			}
			boolean transportMappingFlag = CommonUtilities.isNotNull(transportMapping);
			if (transportMappingFlag) {
				transportMapping.close();
			}
		}catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private String getResponseEvnet(OID oid) throws OCCPDUCreationException {
		try {
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(oid));
			pdu.setType(PDU.GET);
			
			responseEvent = snmp.send(pdu, getTarget(), null);
			boolean responseEventFlag = CommonUtilities.isNotNull(responseEvent);
			if (responseEventFlag){
			if(responseEvent.getPeerAddress()== null && responseEvent.getResponse()==null && responseEvent.getUserObject()!=null){
				result="no result found ";
				formatedValue="-1";
				resultDesc="Dont have Permissions , Due to Security reasons or Fire wall Block or Invalid Community String/Port.";
			}
			else if(responseEvent.getPeerAddress()== null && responseEvent.getResponse()==null && responseEvent.getUserObject()==null){
				result="no result found";
				formatedValue="-1";
				resultDesc="Please check for valid Ip Address and Port is not reachable/Service is not running.";
			}
			else if (responseEventFlag && responseEvent.getResponse()!=null) {
				result= responseEvent.getResponse().get(0).getVariable().toString();
				if(result.equals("noSuchObject")){
					formatedValue="-1";
					resultDesc="Invalid OID, Please check for valid OID.";
				}else{
					formatedValue=result;
					resultDesc=result;
				}
				
			} else {
				resultDesc="Please provide valid device configuration ";
				formatedValue="-1";
				result="no result found";
			}
		}
			return result;
			
		}catch (NullPointerException e) {
			result=e.getMessage();
			formatedValue="-1";
			resultDesc="Invalid Ip Address.";
			//org.snmp4j.MessageException: Unsupported message processing model: 2
			LOGGER.error("",e);
			throw new OCCPDUCreationException("","Error while sending the PDU", e.getMessage());
		} catch (OCCComunityTargetException e) {
			LOGGER.error("",e);
			throw new OCCPDUCreationException("", e.getUserMessage(), e.getMessage());
		}catch (MessageException e) { //org.snmp4j.MessageException: Invalid first sub-identifier (must be 0, 1, or 2) occur in case if oid will not starts with o,1 or 2
			String errorMessage=e.getMessage();
			if(null!=errorMessage && errorMessage.contains("Invalid first sub-identifier")){
				result=e.getMessage();
				formatedValue="-1";
				resultDesc="Invalid OID, Please check for valid OID.";
			}
			LOGGER.error("",e);
			throw new OCCPDUCreationException("", "Invalid OID, Please check for valid OID.", e.getMessage());
		}catch (Exception e) {
			result=e.getMessage();
			formatedValue="-1";
			resultDesc="Please check for valid Ip Address or Community String or Snmp Version .";
			//org.snmp4j.MessageException: Unsupported message processing model: 2
			LOGGER.error("",e);
			throw new OCCPDUCreationException("","Error while sending the PDU", e.getMessage());
		}
	}
	
	private CommunityTarget getTarget() throws OCCComunityTargetException{
		CommunityTarget target = null;
		try{
			Address targetAddress = GenericAddress.parse(address);
			target = new CommunityTarget();
			boolean communityStringFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetail.getCommunityString());
			if (communityStringFlag) {
				target.setCommunity(new OctetString(checkPointDetail.getCommunityString()));
			} else {
				target.setCommunity(new OctetString(COMMUNITY_STRING));
			}
			target.setAddress(targetAddress);
			target.setRetries(Integer.parseInt(KeyValue.SNMP_RETRIES));
			target.setTimeout(Long.parseLong(KeyValue.SNMP_TIMEOUT));
			target.setVersion(Integer.parseInt(checkPointDetail.getSnmpVersion()));
		}catch (NumberFormatException e) {
			result=e.getMessage();
			formatedValue="-1";
			resultDesc="Invalid snmp version, Do not use characters for the version.";
			LOGGER.error("",e);
			throw new OCCComunityTargetException("", "Error while setting the target for the sender", e.getMessage());
		}
		catch (Exception e) {
			result=e.getMessage();
			formatedValue="-1";
			resultDesc="Please check for valid Ip Address or Community String or Snmp Version .";
			LOGGER.error("",e);
			throw new OCCComunityTargetException("", "Error while setting the target for the sender", e.getMessage());
		}
		return target;
	}
	
	/*private String getAsString(OID oid) throws IOException {
		responseEvent = getResponseEvnet(oid);
		return responseEvent.getResponse().get(0).getVariable().toString();
	}*/
}
