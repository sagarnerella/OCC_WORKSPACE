package com.osi.agent.snmp;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentException;
import com.osi.agent.exception.OCCAgentSNMPException;
import com.osi.agent.exception.OCCComunityTargetException;
import com.osi.agent.exception.OCCPDUCreationException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class SNMPWalkMonitor implements IMonitor {

	private static final Logger LOGGER = Logger.getLogger(SNMPWalkMonitor.class);
	private static final String COMMUNITY_STRING=KeyValue.MANAGER_COMMUNITY_STRING;
	private Snmp snmp = null;
	private String address = null;
	private CheckPointDetails checkPointDetail = null;
	@SuppressWarnings("rawtypes")
	private TransportMapping transportMapping=null;
	//private ResponseEvent responseEvent = null;
	
	@Override
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentSNMPException {
		//String result= null;
		CheckpointResult checkpointResult = null;
		//boolean resultFlag = false;
		try {
			if(checkPointDetails.isValid(checkPointDetails)){	
				checkPointDetail = checkPointDetails;
				boolean checkPointDetailFlag = CommonUtilities.isNotNull(checkPointDetail);
				if(checkPointDetailFlag){
					address = "udp:"+checkPointDetail.getSnmpIpAddress().trim()+"/"+checkPointDetail.getSnmpPort();
				start();
				checkpointResult = getResponseEvnet(new OID(checkPointDetail.getCheckPoint().trim()), checkPointDetails);
				//result = processResponse(responseMap, checkPointDetails);
				/*resultFlag = CommonUtilities.isNotNullAndEmpty(result);
				if(resultFlag){
					checkpointResult = new CheckpointResult();
					checkpointResult.setCheckPointresult(result);
					checkpointResult.setCheckPointDetails(checkPointDetails);
				}*/
			}
			}
		} catch (OCCPDUCreationException e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPException("", e.getUserMessage(), e.getMessage());
		} catch (OCCAgentException e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPException("", e.getUserMessage(), e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCAgentSNMPException("", "Error while getting the agent performCheck", e.getMessage());
		} finally{
			/*if(!resultFlag){
				checkpointResult=new CheckpointResult();
				checkpointResult.setCheckPointresult("Device Not Connected");
			}*/
			destroy();
		}
		return checkpointResult;
	}
	
	/*private String processResponse(Map<OID, Variable> responseMap, CheckPointDetails checkPointDetails) {
		String result = null;
		ScriptEngineManager mgr = new ScriptEngineManager();
	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	    String walkAlogirthm = checkPointDetails.getSnmpWalkAlgorithm();
	    LOGGER.info(engine.eval(foo));
		return result;
	}*/

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
	
	private CheckpointResult getResponseEvnet(OID oidString, CheckPointDetails resCheckPointDetails) throws OCCPDUCreationException {
		CheckpointResult checkpointResult = null;
		boolean resultFlag = true;
		String snmpWalkAlgorithm = null;
		ScriptEngine engine = null;
		//PDU response = null;
		OID oid = null;
		try {
			checkpointResult = new CheckpointResult();
			try{
				oid = new OID(oidString);
			} catch(RuntimeException ex){
				LOGGER.info("OID is not specified correctly.");
				checkpointResult.setCheckPointresult("OID is not specified correctly.");
				resCheckPointDetails.setCheckType('E');
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
				resultFlag = false;
		    }
			
			TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
			CommunityTarget target=getTarget();
			OID rootOID=oid;
			List<TreeEvent> events=null;
			SnmpWalkThread snmpWalkThread=new SnmpWalkThread(treeUtils,events,target,rootOID);
			snmpWalkThread.start();
			snmpWalkThread.join(10000);
			events=snmpWalkThread.getEventList();
		    //List<TreeEvent> events = treeUtils.getSubtree(getTarget(), oid);
		    if(events == null || events.size() == 0){
		    	LOGGER.info("No result returned.");
		    	checkpointResult.setCheckPointresult("No result returned.");
		    	checkpointResult.setFormatedValue("-1");
		    	resCheckPointDetails.setCheckType('E');
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
				resultFlag = false;
		    }
		    
		    // For Evaluating the String formula
        	ScriptEngineManager mgr = new ScriptEngineManager();
    	    engine = mgr.getEngineByName("JavaScript");
    	    snmpWalkAlgorithm = resCheckPointDetails.getSnmpWalkAlgorithm();
		    boolean evaluetFlag = false;
		    // Get snmpwalk result.
		    for (TreeEvent event : events) {
		      if(event != null){
		        if (event.isError()) {
		        	LOGGER.info("oid [" + oid + "] " + event.getErrorMessage());
		          }
		            
		        VariableBinding[] varBindings = event.getVariableBindings();
		        if(varBindings == null || varBindings.length == 0){
		        	LOGGER.info("No result returned.");
		          checkpointResult.setCheckPointresult("No result returned.");
		          checkpointResult.setFormatedValue("-1");
		          resCheckPointDetails.setCheckType('E');
		          checkpointResult.setResultDesc("No result returned.");
		          checkpointResult.setCheckPointDetails(resCheckPointDetails);
		          resultFlag = false;
		        } else {
			        for (VariableBinding varBinding : varBindings) {
			        	snmpWalkAlgorithm = snmpWalkAlgorithm.replaceAll("\\b"+varBinding.getOid()+"\\b", varBinding.getVariable().toString()); // replacing OIDs with response in formula
			        }
			        evaluetFlag = true;
		        }
		      }
		      

		      /*if(event != null){
		        if(event.isError()==true && (event.getVariableBindings() == null || event.getVariableBindings().length == 0)){
		        	  LOGGER.info("No result returned.");
			          checkpointResult.setCheckPointresult("No result returned.");
			          resCheckPointDetails.setCheckType('E');
			          checkpointResult.setCheckPointDetails(resCheckPointDetails);
			          resultFlag = false;
			        }
		        else {
		        	 VariableBinding[] varBindings = event.getVariableBindings();
			        for (VariableBinding varBinding : varBindings) {
			        	snmpWalkAlgorithm = snmpWalkAlgorithm.replaceAll("\\b"+varBinding.getOid()+"\\b", varBinding.getVariable().toString()); // replacing OIDs with response in formula
			        }
			        evaluetFlag = true;
		        }
		      }*/
		    
		      
		      
		      
		    }
		    
		    if(null != engine && null != snmpWalkAlgorithm && evaluetFlag) {
		    	try {
					Object result = engine.eval(snmpWalkAlgorithm); // Evaluating String formula
					if (null != result && "NaN".equalsIgnoreCase(result.toString())) {
						checkpointResult.setCheckPointresult("0");
						checkpointResult.setFormatedValue("-1");
						checkpointResult.setResultDesc("Result is not a number");
					} else {
						checkpointResult.setCheckPointresult(result.toString());
						checkpointResult.setFormatedValue(result.toString());
						checkpointResult.setResultDesc(result.toString());
					}
					
					checkpointResult.setCheckPointDetails(resCheckPointDetails);
					resultFlag = false;
				} catch (Exception e) {
					checkpointResult.setCheckPointresult("Failed to execute the alogorithm");
					checkpointResult.setFormatedValue("-1");
					resCheckPointDetails.setCheckType('E');
					checkpointResult.setResultDesc("Failed to execute the alogorithm");
					checkpointResult.setCheckPointDetails(resCheckPointDetails);
					resultFlag = false;
					LOGGER.error("Exception Occured While Eavaluating the Algorithm :: ", e);
				}
		    } /*else {
		    	checkpointResult.setCheckPointresult("Failed to execute the alogorithm");
		    	resCheckPointDetails.setCheckType('E');
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
				resultFlag = false;
		    }*/
			
			
			/*PDU pdu = new PDU();
			pdu.add(new VariableBinding(oid));
			pdu.setType(PDU.GETBULK);
			
			responseEvent = snmp.send(pdu, getTarget(), null);
			boolean responseEventFlag = CommonUtilities.isNotNull(responseEvent);
			if (responseEventFlag)
				response = responseEvent.getResponse();
			if (response == null) {
				checkpointResult.setCheckPointresult("SNMP Response TimeOut...");
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
				resultFlag = false;
		        LOGGER.error("TimeOut...");
		    } else {
		        if (response.getErrorStatus() == PDU.noError) {
		        	// For Evaluating the String formula
		        	ScriptEngineManager mgr = new ScriptEngineManager();
		    	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
		    	    String snmpWalkAlgorithm = resCheckPointDetails.getSnmpWalkAlgorithm();
		            
		    	    Vector<? extends VariableBinding> vbs = response.getVariableBindings();
		            for (VariableBinding vb : vbs) {
		            	snmpWalkAlgorithm.replace(vb.getOid().toString(), vb.getVariable().toString()); // replacing OIDs with response in formula
		            }
		            try {
						Object result = engine.eval(snmpWalkAlgorithm); // Evaluating String formula
						checkpointResult.setCheckPointresult(result.toString());
						checkpointResult.setCheckPointDetails(resCheckPointDetails);
						resultFlag = false;
					} catch (Exception e) {
						checkpointResult.setCheckPointresult("Failed to execute the alogorithm");
						checkpointResult.setCheckPointDetails(resCheckPointDetails);
						resultFlag = false;
						LOGGER.error("Exception Occured While Eavaluating the Algorithm :: ", e);
					}
		        } 
		        else 
		        {
		        	checkpointResult.setCheckPointresult(response.getErrorStatusText());
					checkpointResult.setCheckPointDetails(resCheckPointDetails);
					resultFlag = false;
		        	LOGGER.error("Error:" + response.getErrorStatusText());
		        }
		    }*/
			
		} catch (OCCComunityTargetException e) {
			LOGGER.error("",e);
			throw new OCCPDUCreationException("", e.getUserMessage(), e.getMessage());
		}catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCPDUCreationException("","Error while sending the PDU", e.getMessage());
		} finally {
			if(resultFlag){
				checkpointResult.setCheckPointresult("Failed to get the result from destination server with provided base OID");
				checkpointResult.setFormatedValue("-1");
				checkpointResult.setResultDesc("Failed to get the result from destination server with provided base OID");
				resCheckPointDetails.setCheckType('E');
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
			}
		}
		return checkpointResult;
	}
	
	private CommunityTarget getTarget() throws OCCComunityTargetException{
		CommunityTarget target = null;
		try{
			Address targetAddress = GenericAddress.parse(address);
			target = new CommunityTarget();
			boolean communityStringFlag = CommonUtilities.isNotNullAndEmpty(checkPointDetail.getCommunityString());
			if (communityStringFlag) {
				target.setCommunity(new OctetString(checkPointDetail.getCommunityString().toLowerCase()));
			} else {
				target.setCommunity(new OctetString(COMMUNITY_STRING));
			}
			target.setAddress(targetAddress);
			target.setRetries(Integer.parseInt(KeyValue.SNMP_RETRIES));
			target.setTimeout(Long.parseLong(KeyValue.SNMP_TIMEOUT));
			target.setVersion(Integer.parseInt(checkPointDetail.getSnmpVersion()));
		}catch (Exception e) {
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
