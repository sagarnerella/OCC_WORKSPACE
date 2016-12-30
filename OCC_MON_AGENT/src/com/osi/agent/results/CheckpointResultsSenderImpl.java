/**
 * 
 */
package com.osi.agent.results;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.domain.DeviceCheckpointResult;
import com.osi.agent.domain.DeviceCheckpointResults;
import com.osi.agent.exception.OCCAgentSNMPSenderException;
import com.osi.agent.exception.OCCCheckpointResultsException;
import com.osi.agent.exception.OCCXMLFormatException;
import com.osi.agent.snmp.adapter.SNMPAgentSender;
import com.osi.agent.vo.CheckpointResult;

/**
 * @author jkorada1
 *
 */
public class CheckpointResultsSenderImpl implements ICheckpointResultsSender {

	private static final Logger LOGGER = Logger.getLogger(CheckpointResultsSenderImpl.class);

	//SNMPAgentSender sender = null;
	ICacheManager cacheManager = null;

	/*public CheckpointResultsSenderImpl(){
		sender = new SNMPAgentSender();
	}*/

	public CheckpointResultsSenderImpl(ICacheManager cacheManager){
		this.cacheManager = cacheManager;
		//sender = new SNMPAgentSender();
	}

	@Override
	public boolean sendResults() throws OCCCheckpointResultsException {
		boolean flag = false;
		List<CheckpointResult> checkpointResultList = new ArrayList<CheckpointResult>(0);
		try {
			List<CheckpointResult> resultsCache=new ArrayList<CheckpointResult>(cacheManager.getResultCache());
			checkpointResultList = CommonUtilities.deserializeCheckpointResultsFromFile();
			boolean resultsCacheFlag = CommonUtilities.isNotNull(resultsCache);
			boolean checkpointResultListFlag = CommonUtilities.isNotNull(resultsCache);
			if(resultsCacheFlag && !resultsCache.isEmpty())
				checkpointResultList.addAll(resultsCache);
			if(checkpointResultListFlag && !checkpointResultList.isEmpty()){
				DeviceCheckpointResults deviceCheckpointResults = getResultsFromResultsCache(checkpointResultList);
				String resultString = CommonUtilities.getXMLFromObject(deviceCheckpointResults, DeviceCheckpointResults.class);
				//flag = sender.send(KeyValue.AGENT_RESULT_OID,resultString);
				if(!flag){
					CommonUtilities.serializeCheckPointResultFromMemory(checkpointResultList);
				}
				cacheManager.removeResultsFromCache(checkpointResultList);
			}else{
				LOGGER.info("No Results to process");
			}
		} catch (OCCXMLFormatException e) {
			flag = false;
			throw new OCCCheckpointResultsException("", e.getUserMessage(), e.getMessage());
		} /*catch (OCCAgentSNMPSenderException e) {
			flag = false;
			throw new OCCCheckpointResultsException("", e.getUserMessage(), e.getMessage());
		}*/ catch (Exception e) {
			flag = false;
			LOGGER.error("",e);
			throw new OCCCheckpointResultsException("", "Error occured while sending results", e.getMessage());
		}
		return flag;
	}



	private DeviceCheckpointResults getResultsFromResultsCache(List<CheckpointResult> resultsCache){
		DeviceCheckpointResults checkpointResults=null;
		List<DeviceCheckpointResult> checkpointResultList=null;
		boolean checkpointResultListFlag = CommonUtilities.isNotNull(resultsCache);
		if(checkpointResultListFlag && !resultsCache.isEmpty()){
			checkpointResults = new DeviceCheckpointResults();
			checkpointResultList = new ArrayList<DeviceCheckpointResult>(0);
			for (CheckpointResult checkpointResult : resultsCache) {
				boolean checkpointResultFlag = CommonUtilities.isNotNull(checkpointResult);
				if (checkpointResultFlag)
					checkpointResultList.add(setCheckpointResult(checkpointResult));
			}
			checkpointResults.setCheckpointResult(checkpointResultList);
		}
		return checkpointResults;
	}

	private DeviceCheckpointResult setCheckpointResult(CheckpointResult result){
		DeviceCheckpointResult checkpointResult = new DeviceCheckpointResult();
		checkpointResult.setDeviceId(result.getCheckPointDetails().getDeviceID());
		checkpointResult.setCheckpointId(result.getCheckPointDetails().getCheckpointID());
		checkpointResult.setValue(result.getCheckPointresult());
		checkpointResult.setCheckpointExecutionTime(result.getExecutinTime());
		checkpointResult.setDowntimeScheduled(result.getDowntimeScheduled());
		checkpointResult.setConnectType(result.getConnectType());
		checkpointResult.setResultDesc(result.getResultDesc());
		if("noSuchObject".equalsIgnoreCase(result.getCheckPointresult()) || "Device Not Connected".equalsIgnoreCase(result.getCheckPointresult())){
			checkpointResult.setCheckType('E');
		}else{
			checkpointResult.setCheckType(result.getCheckPointDetails().getCheckType());
		}
		checkpointResult.setStatus('N');
		return checkpointResult;
	}
}
