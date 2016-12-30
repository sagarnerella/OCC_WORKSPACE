package com.osi.agent;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.domain.DeviceCheckpointResult;
import com.osi.agent.domain.DeviceCheckpointResults;
import com.osi.agent.exception.OCCXMLFormatException;
import com.osi.agent.vo.CheckpointResult;

public class TestJava {

	/**
	 * @param args
	 * @throws JAXBException 
	 * @throws OCCXMLFormatException 
	 */
	public static void main(String[] args) throws JAXBException, OCCXMLFormatException {
		// TODO Auto-generated method stub
		
		List<CheckpointResult> checkpointResult = new ArrayList<CheckpointResult>();
		
			String xml = "";
			DeviceCheckpointResults deviceCheckpointResults = getResultsFromResultsCache(checkpointResult);
			xml = CommonUtilities.getXMLFromObject(deviceCheckpointResults, DeviceCheckpointResults.class);
	}

	private static DeviceCheckpointResults getResultsFromResultsCache(List<CheckpointResult> resultsCache){
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
	private static DeviceCheckpointResult setCheckpointResult(CheckpointResult result){
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
