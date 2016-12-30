package com.osi.re.jobs;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.common.config.constants.MSConstants;
import com.osi.common.util.LoadKnowErrors;
import com.osi.common.util.MSRegularExpressions;
import com.osi.re.common.Base64;
import com.osi.re.common.CommonUtilities;
import com.osi.re.dao.IREDao;
import com.osi.re.dao.REDao;
import com.osi.re.exception.OCCObjectFormatException;
import com.osi.re.vo.CheckpointResult;
import com.osi.re.vo.Result;

public class RERegularResultMonitorJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(RERegularResultMonitorJob.class);
	
	private static final String ROWSET_START = "<ROWSET>";
	private static final String ROW_START = "<ROW>";
	private static final String RESULT_VALUE_START = "<Result_Value>";
	private static final String DESCRIPTION_START = "<Description>";
	private static final String ROWSET_END = "</ROWSET>";
	private static final String ROW_END = "</ROW>";
	private static final String RESULT_VALUE_END = "</Result_Value>";
	private static final String DESCRIPTION_END = "</Description>";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("RERegularResultMonitorJob fired at :: "+Calendar.getInstance().getTime());
		List<Integer> ctresultIds = null;
		List<Integer> rpresultIds = null;
		IREDao reDao = null;
		boolean flag=true;
		List<CheckpointResult> checkpointResults = null;
		try {
			reDao = new REDao();
			checkpointResults = reDao.fetchResults("R");
			if (null != checkpointResults && checkpointResults.size() > 0) {
				ctresultIds = new ArrayList<Integer>(0);
				rpresultIds = new ArrayList<Integer>(0);
				for (CheckpointResult checkpointResult : checkpointResults) {
					ctresultIds.add(checkpointResult.getResultId());
				}
			}
			if (null != ctresultIds && ctresultIds.size() > 0) {
				reDao.updateResultProcessStatus(ctresultIds, "I");
			}
			for (CheckpointResult checkpointResult : checkpointResults) {
				try {
					String resultValue = checkpointResult.getValue();
					if(isXMLResult(checkpointResult.getConnectType())){
						String responseValue = "";
						String resultDesc="";
						if (resultValue.indexOf("<RESULT>") == -1) {
							if (checkpointResult.getConnectType().equalsIgnoreCase(CommonUtilities.getProperty("SSH"))) {
								responseValue = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG><ROWSET><ROW><Decription>Invalid Script, please revisit the script that we configured </Decription></ROW></ROWSET></MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
							} else {
								responseValue = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Result not found, please check the configuration parameters</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
							}
						} else {
							responseValue = resultValue;
						}
						LOGGER.info("The resultant response value is :::::::::: "+responseValue);
						Result jResult=(Result)getObjectFromXML(responseValue, Result.class);
							boolean checkIncidentFlag =  jResult.isISINCIDENT();
							if (checkIncidentFlag) {
								if (checkpointResult.getConnectType().equalsIgnoreCase("SSH") && (resultValue.indexOf("<ROWSET>") != -1)) {
									checkpointResult.setResultDesc(resultValue.substring(resultValue.indexOf("<ROWSET>"), resultValue.lastIndexOf("</ROWSET>")+9));
								}/* else if (null != jResult.getMSG()) {
									checkpointResult.setResultDesc(Base64.decode(jResult.getMSG()));
								} 
								*/
								//the above code is commented to show proper error messages
								// the below code added newly to show proper error messages start
								/*else if (checkpointResult.getConnectType().equalsIgnoreCase("SSH") && jResult.getFORMATTEDVALUE().equals("-1")) {
									 resultDesc=ROWSET_START+ROW_START+DESCRIPTION_START+checkpointResult.getResultDesc()+DESCRIPTION_END+ROW_END+ROWSET_END;
									//checkpointResult.setResultDesc(Base64.decode(jResult.getMSG()));
									checkpointResult.setResultDesc(resultDesc);
								}*/ else {
									 resultDesc=ROWSET_START+ROW_START+DESCRIPTION_START+checkpointResult.getResultDesc()+DESCRIPTION_END+ROW_END+ROWSET_END;
									checkpointResult.setResultDesc(resultDesc);
								}
								//end
								checkpointResult.setFormatedValue(jResult.getFORMATTEDVALUE());
								reDao.saveCheckpointAlert(checkpointResult,"RAISE_INCIDENT");
							}
							//reDao.updateResultProcessStatus(checkpointResult.getResultId());
							rpresultIds.add(checkpointResult.getResultId());
					}
					else if (null != checkpointResult.getFormatedValue() && MSRegularExpressions.calculateRelationalOperators(checkpointResult.getFormatedValue(), checkpointResult.getThreasholdValue(), checkpointResult.getThreasholdCondition())) {
						LOGGER.info("Regular Expresson checking Value ::: "+checkpointResult.getFormatedValue()+" and Threshold Vlaues are ::: "+checkpointResult.getThreasholdValue());
						boolean checkAlertEntry =  reDao.checkCheckpointAlertEntry(checkpointResult.getDeviceId(), checkpointResult.getCheckpointId());
						if (!checkAlertEntry) {
							String resultDesc = "";
							/*if(!CommonUtilities.getProperty("SNMP_TABLE").equalsIgnoreCase(checkpointResult.getConnectType()))
							resultDesc = ROWSET_START+ROW_START+RESULT_VALUE_START+checkpointResult.getFormatedValue()+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointResult.getAssetName()+DESCRIPTION_END+ROW_END+ROWSET_END;
							else*/
							String thresholdValue="Threshold Value: "+checkpointResult.getThreasholdValue()+",";
							if(checkpointResult.getFormatedValue().equalsIgnoreCase("-1")){
							resultDesc = ROWSET_START+ROW_START+RESULT_VALUE_START+checkpointResult.getFormatedValue()+RESULT_VALUE_END+DESCRIPTION_START+checkpointResult.getResultDesc()+DESCRIPTION_END+ROW_END+ROWSET_END;
							checkpointResult.setResultDesc(resultDesc);
							reDao.saveCheckpointAlert(checkpointResult,"RAISE_INCIDENT");
							}
							else{
								thresholdValue=thresholdValue+" ,";
							resultDesc = ROWSET_START+ROW_START+RESULT_VALUE_START+checkpointResult.getFormatedValue()+RESULT_VALUE_END+DESCRIPTION_START+thresholdValue+checkpointResult.getResultDesc()+DESCRIPTION_END+ROW_END+ROWSET_END;
							//resultDesc = ROWSET_START+ROW_START+RESULT_VALUE_START+checkpointResult.getFormatedValue()+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointResult.getAssetName()+" for "+checkpointResult.getResultDesc()+DESCRIPTION_END+ROW_END+ROWSET_END;	
							checkpointResult.setResultDesc(resultDesc);
							reDao.saveCheckpointAlert(checkpointResult,"ACTIVE");
							}
						}
						//reDao.updateResultProcessStatus(checkpointResult.getResultId());
						rpresultIds.add(checkpointResult.getResultId());
					} else {
						rpresultIds.add(checkpointResult.getResultId());
						//reDao.updateResultProcessStatus(checkpointResult.getResultId());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Map<String,String> knowErrorsMap=LoadKnowErrors.getKnowErrors();
					if(knowErrorsMap.containsKey(checkpointResult.getFormatedValue())){
						String resultDesc = ROWSET_START+ROW_START+RESULT_VALUE_START+"noSuchInstance OID does not exist "+RESULT_VALUE_END+DESCRIPTION_START+CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointResult.getAssetName()+DESCRIPTION_END+ROW_END+ROWSET_END;
						checkpointResult.setResultDesc(resultDesc);
						reDao.saveCheckpointAlert(checkpointResult,"RAISE_INCIDENT");
					rpresultIds.add(checkpointResult.getResultId());
					}
					if (null != reDao && null != rpresultIds && rpresultIds.size() > 0) {
						reDao.updateResultProcessStatus(rpresultIds,"P");
					}
					if (null != reDao && null != ctresultIds && ctresultIds.size() > 0){
						reDao.updateIntermediateResultProcess(ctresultIds);
					}
				
				}
			}
			if (null != rpresultIds && rpresultIds.size() > 0) {
				reDao.updateResultProcessStatus(rpresultIds,"P");
			}
			LOGGER.info("Fired RERegularResultMonitorJob "+checkpointResults.toString());
		} catch (Exception e) {
			try {
				if (null != reDao && null != rpresultIds && rpresultIds.size() > 0) {
					reDao.updateResultProcessStatus(rpresultIds,"P");
				}
				if (null != reDao && null != ctresultIds && ctresultIds.size() > 0){
					reDao.updateIntermediateResultProcess(ctresultIds);
				}
			} catch (Exception e1) {
				LOGGER.error("",e1);
			}
			LOGGER.error("Error occurred while executing RERegularResultMonitorJob",e);
		}
		LOGGER.info("RERegularResultMonitorJob completed execution at :: "+Calendar.getInstance().getTime());
	}

	private static <T> Object getObjectFromXML(String xml, Class<T> clazz) throws OCCObjectFormatException, Exception {
		Object object=null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller un = context.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			object = un.unmarshal(reader);
		} catch (JAXBException e) {
			LOGGER.error("",e);
			throw new OCCObjectFormatException("", "Error occured while converting XML to Object", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCObjectFormatException("", "Error occured while converting XML to Object", e.getMessage());
		}
		return object;
	}
	
	private static boolean isXMLResult(String connectType) throws Exception {
		boolean xmlResultFalg = false;
		 if (connectType.equalsIgnoreCase("JDBC")) {
			 xmlResultFalg = true;
		 } else if (connectType.equalsIgnoreCase("LogMiner")) {
			 xmlResultFalg = true;
		 } else if (connectType.equalsIgnoreCase("SSH")) {
			 xmlResultFalg = true;
		 } 
		return xmlResultFalg;
	}
}
