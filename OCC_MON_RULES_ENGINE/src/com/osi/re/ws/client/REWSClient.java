package com.osi.re.ws.client;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.osi.common.config.constants.MSConstants;
import com.osi.re.common.CommonUtilities;
import com.osi.re.exception.WSException;
import com.osi.re.vo.CheckpointAlert;
import com.osi.re.ws.stub.CreateIncidentRequestType;
import com.osi.re.ws.stub.CreateIncidentResponseType;
import com.osi.re.ws.stub.Server;
import com.osi.re.ws.stub.ServerPortType;

public class REWSClient {
	private static final Logger LOGGER = Logger.getLogger(REWSClient.class);
	public static CreateIncidentResponseType createWSRequest(CheckpointAlert checkpointAlert) throws WSException {
		LOGGER.info("REWSClient::createWSRequest:Start");
		CreateIncidentResponseType createIncidentResponseType = null;
		try {
			Server server = new Server();
			ServerPortType serverPortType = server.getServerPort();
			CreateIncidentRequestType createIncidentRequestType = new CreateIncidentRequestType();
			createIncidentRequestType.setAssetId(checkpointAlert.getAssetId());
			createIncidentRequestType.setCheckpointId(checkpointAlert.getMasterCheckpointId());
			createIncidentRequestType.setTktTitle(CommonUtilities.getProperty("INCIDENT_TITLE")+" "+checkpointAlert.getAssetName());
			createIncidentRequestType.setTktLastChkUpdatedDate(getGregorianTime(checkpointAlert.getCheckpointExecutionTime()));
			if(null != checkpointAlert.getResultDesc()  && !"".equalsIgnoreCase(checkpointAlert.getResultDesc())) {
				createIncidentRequestType.setTktDesc(checkpointAlert.getResultDesc());
			} else {
				createIncidentRequestType.setTktDesc(CommonUtilities.getProperty("INCIDENT_DESC")+" "+checkpointAlert.getAssetName());
			}
			// added for snmptable
			createIncidentRequestType.setTkt_dev_checkpoint_id(checkpointAlert.getDeviceCheckpointId());
			
			createIncidentResponseType = serverPortType.createIncident(createIncidentRequestType);
		} catch (Exception e) {
			throw new WSException(MSConstants.ERROR_CODE_7004, "Error occured while calling raise incidents web service", e.getMessage());
		}
		LOGGER.info("REWSClient::createWSRequest:End");
		return createIncidentResponseType;
	} 
	private static XMLGregorianCalendar getGregorianTime(Timestamp checkExecutionTime) {
		XMLGregorianCalendar xgcal = null;
		Date frmDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (null != checkExecutionTime) {
				frmDate = dateFormat.parse(""+checkExecutionTime);
			} else {
				frmDate = dateFormat.parse(""+getTodayTimestamp());
			}
			
			GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
			gcal.setTime(frmDate);
			xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return xgcal;
	}
	
	private static Timestamp getTodayTimestamp() {
		java.util.Date date = null;
		java.sql.Timestamp timeStamp = null;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			java.sql.Date dt = new java.sql.Date(calendar.getTimeInMillis());
			java.sql.Time sqlTime = new java.sql.Time(calendar.getTime()
					.getTime());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = simpleDateFormat.parse(dt.toString() + " "+ sqlTime.toString());
			timeStamp = new java.sql.Timestamp(date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
	
	/*public static void main(String[] args) {
		LOGGER.info(getGregorianTime(getTodayTimestamp()));
	}*/
	
}
