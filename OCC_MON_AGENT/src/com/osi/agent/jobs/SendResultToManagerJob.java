package com.osi.agent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.domain.DeviceCheckpointResult;
import com.osi.agent.domain.DeviceCheckpointResults;
import com.osi.agent.exception.OCCAgentDeserializeException;
import com.osi.agent.exception.OCCAgentSerializeException;
import com.osi.agent.exception.OCCResultsCacheException;
import com.osi.agent.exception.OCCXMLFormatException;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.vo.CheckpointResult;

public class SendResultToManagerJob implements Job {
	private static final Logger LOGGER = Logger
			.getLogger(SendResultToManagerJob.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOGGER.info("SendResultToManagerJob :: execute :: start ");
		LOGGER.info("SendResultToManagerJob :: execute :: start ");
		ICacheManager cacheManager = (ICacheManager) context.getJobDetail()
				.getJobDataMap().get("CACHE_MANAGER");

		String managerHeartBeat = CommonUtilities
				.getProperty("HEART_BEAT_SUCCESS");//cacheManager.getManagerHeartBeat();
		try {
			if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_FAILURE"))) {
				List<CheckpointResult> checkPointResultList = new ArrayList<CheckpointResult>(
						cacheManager.getResultCache());
				boolean checkPointResultListFlag = CommonUtilities
						.isNotNull(checkPointResultList);
				if (checkPointResultListFlag && !checkPointResultList.isEmpty()) {
					boolean flag = CommonUtilities
							.serializeCheckPointResultFromMemory(checkPointResultList);
					if (flag) {
						cacheManager
								.removeResultsFromCache(checkPointResultList);
					}
				}
			} else if (managerHeartBeat != null
					&& managerHeartBeat.equalsIgnoreCase(CommonUtilities
							.getProperty("HEART_BEAT_SUCCESS"))) {

				String resultXml = "";
				List<CheckpointResult> checkpointResultList = new ArrayList<CheckpointResult>(
						0);
				List<CheckpointResult> resultsCache = new ArrayList<CheckpointResult>(
						cacheManager.getResultCache());
				checkpointResultList = CommonUtilities
						.deserializeCheckpointResultsFromFile();

				boolean resultsCacheFlag = CommonUtilities
						.isNotNull(resultsCache);
				boolean checkpointResultListFlag = CommonUtilities
						.isNotNull(resultsCache);
				if (resultsCacheFlag && !resultsCache.isEmpty())
					checkpointResultList.addAll(resultsCache);
				if (checkpointResultListFlag && !checkpointResultList.isEmpty()) {
					DeviceCheckpointResults deviceCheckpointResults = getResultsFromResultsCache(checkpointResultList);
					resultXml = CommonUtilities.getXMLFromObject(
							deviceCheckpointResults,
							DeviceCheckpointResults.class);
					boolean resultXmlFlag = CommonUtilities
							.isNotNullAndEmpty(resultXml);
					if (resultXmlFlag) {
						String managerUrl = "";
						managerUrl = CommonUtilities
								.getProperty("SENDRESULT_TOMANAGER_URL_OF_MANAGER");
						String outPut = CommonUtilities.getDataFromManager(
								managerUrl, resultXml,"SendResultToManagerJob");
						boolean outputFlag = CommonUtilities
								.isNotNullAndEmpty(outPut);
						if (outputFlag
								&& outPut.equalsIgnoreCase(CommonUtilities
										.getProperty("HEART_BEAT_SUCCESS"))) {
							System.out
									.println("Result sent successfully and remove from Cache ");
							LOGGER.info("result sent successfully and remove from Cache ");
							cacheManager
									.removeResultsFromCache(checkpointResultList);
						} else {
							boolean flag=CommonUtilities
									.serializeCheckPointResultFromMemory(checkpointResultList);
							if (flag) {
								cacheManager
										.removeResultsFromCache(checkpointResultList);
							}
							
							LOGGER.info(outPut);
							LOGGER.info("Erro while Storing the Result in Manager");
						}
					}
				}
			}
		} catch (OCCXMLFormatException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occured while converting XML to Object" , e);
			e.printStackTrace();
		} catch (OCCResultsCacheException e) {
			// TODO Auto-generated catch block
			LOGGER.error("OCCResultsCacheException ", e);
			e.printStackTrace();
		} catch (OCCAgentSerializeException e) {
			// TODO Auto-generated catch block
			LOGGER.error(
					"OCCAgentSerializeException in SendResultToManagerJob", e);
			e.printStackTrace();
		} catch (OCCAgentDeserializeException e) {
			// TODO Auto-generated catch block
			LOGGER.error(
					"OCCAgentDeserializeException in SendResultToManagerJob", e);
			e.printStackTrace();
		}
		LOGGER.info("SendResultToManagerJob :: execute :: end ");
		LOGGER.info("SendResultToManagerJob :: execute :: end ");
	}

	private DeviceCheckpointResults getResultsFromResultsCache(
			List<CheckpointResult> resultsCache) {
		DeviceCheckpointResults checkpointResults = null;
		List<DeviceCheckpointResult> checkpointResultList = null;
		boolean checkpointResultListFlag = CommonUtilities
				.isNotNull(resultsCache);
		if (checkpointResultListFlag && !resultsCache.isEmpty()) {
			checkpointResults = new DeviceCheckpointResults();
			checkpointResultList = new ArrayList<DeviceCheckpointResult>(0);
			for (CheckpointResult checkpointResult : resultsCache) {
				boolean checkpointResultFlag = CommonUtilities
						.isNotNull(checkpointResult);
				if (checkpointResultFlag)
					checkpointResultList
							.add(setCheckpointResult(checkpointResult));
			}
			checkpointResults.setCheckpointResult(checkpointResultList);
		}
		return checkpointResults;
	}

	private DeviceCheckpointResult setCheckpointResult(CheckpointResult result) {
		DeviceCheckpointResult checkpointResult = new DeviceCheckpointResult();
		checkpointResult.setDeviceId(result.getCheckPointDetails()
				.getDeviceID());
		checkpointResult.setCheckpointId(result.getCheckPointDetails()
				.getCheckpointID());
		checkpointResult.setValue(result.getCheckPointresult());
		checkpointResult.setCheckpointExecutionTime(result.getExecutinTime());
		checkpointResult.setDowntimeScheduled(result.getDowntimeScheduled());
		checkpointResult.setConnectType(result.getConnectType());
		checkpointResult.setResultDesc(result.getResultDesc());
		checkpointResult.setFormatedValue(result.getFormatedValue());
		if ("noSuchObject".equalsIgnoreCase(result.getCheckPointresult())
				|| "Device Not Connected".equalsIgnoreCase(result
						.getCheckPointresult())) {
			checkpointResult.setCheckType('E');
		} else {
			checkpointResult.setCheckType(result.getCheckPointDetails()
					.getCheckType());
		}
		checkpointResult.setStatus('N');
		return checkpointResult;
	}
}
