package com.osi.agent.jobs;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.jdbc.JDBCMonitor;
import com.osi.agent.jmx.JMXMonitor;
import com.osi.agent.log.LogMonitor;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.results.ICacheManager;
import com.osi.agent.snmp.SNMPMonitor;
import com.osi.agent.snmp.SNMPWalkMonitor;
import com.osi.agent.snmptable.SnmpTableMonitor;
import com.osi.agent.ssh.SSHMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class CheckpointMonitoringJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(CheckpointMonitoringJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CheckpointResult checkPointResult = new CheckpointResult();
		IMonitor jmxmonitor = new JMXMonitor();
		IMonitor snmpmonitor = new SNMPMonitor();
		IMonitor snmpwalkmonitor = new SNMPWalkMonitor();
		IMonitor jdbcmonitor = new JDBCMonitor();
		IMonitor fileMonitor = new LogMonitor();
		IMonitor sshWrapper = new SSHMonitor();
		//Added for SNMPTable
		IMonitor snmpTableMonitor = new SnmpTableMonitor();
		CheckPointDetails checkpointDetails = null;
		boolean executeFlag = false;
		ICacheManager cacheManager = null;
		try {
			checkpointDetails  = (CheckPointDetails) context.getJobDetail().getJobDataMap().get("CHECKPOINT_DETAILS");
			cacheManager  = (ICacheManager) context.getJobDetail().getJobDataMap().get("CACHE_MANAGER");
			boolean startTimeFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getStartTime());
			boolean endTimeFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getEndTime());
			boolean fromDayOfWeekFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getFromDayOfWeek());
			boolean toDayOfWeekFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getToDayOfWeek());
			boolean startDateFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getStartDate());
			boolean endDateFlag = CommonUtilities.isNotNullAndEmpty(checkpointDetails.getEndDate());
			if(startTimeFlag && endTimeFlag){
				if(fromDayOfWeekFlag && toDayOfWeekFlag && startDateFlag && endDateFlag) {
					executeFlag = checkWeekBasedDownTime(checkpointDetails.getStartTime(),checkpointDetails.getEndTime(),checkpointDetails.getFromDayOfWeek(),checkpointDetails.getToDayOfWeek(),checkpointDetails.getStartDate(),checkpointDetails.getEndDate());
				}else if (startDateFlag && endDateFlag){
					executeFlag = checkTimeBasedDownTime(checkpointDetails.getStartTime(),checkpointDetails.getEndTime(),checkpointDetails.getStartDate(),checkpointDetails.getEndDate());
				}
				else if(fromDayOfWeekFlag && toDayOfWeekFlag){
					executeFlag = checkWeekBasedDownTime(checkpointDetails.getStartTime(),checkpointDetails.getEndTime(),checkpointDetails.getFromDayOfWeek(),checkpointDetails.getToDayOfWeek(),null,null);
				}else{
					executeFlag = checkTimeBasedDownTime(checkpointDetails.getStartTime(),checkpointDetails.getEndTime(),null,null);
				}
			}
			if(!executeFlag){
				LOGGER.info("CheckpointMonitoringJob Job fired...");
				if(checkpointDetails != null){
					if(null!=checkpointDetails.getJmxConnectType() && checkpointDetails.getJmxConnectType().equalsIgnoreCase(KeyValue.JMX_CONNECT_TYPE)){   
						checkPointResult = jmxmonitor.performCheck(checkpointDetails);
					} else if(null!=checkpointDetails.getSnmpConnectType() && checkpointDetails.getSnmpConnectType().equalsIgnoreCase(KeyValue.SNMP_CONNECT_TYPE)){  
						checkPointResult = snmpmonitor.performCheck(checkpointDetails);
					} else if(null!=checkpointDetails.getSnmpConnectType() && checkpointDetails.getSnmpConnectType().equalsIgnoreCase(KeyValue.SNMP_WALK_CONNECT_TYPE)){  
						checkPointResult = snmpwalkmonitor.performCheck(checkpointDetails);
					} else if(null!=checkpointDetails.getJdbcConnectType() && checkpointDetails.getJdbcConnectType().equalsIgnoreCase(KeyValue.JDBC_CONNECT_TYPE)){  
						checkPointResult = jdbcmonitor.performCheck(checkpointDetails);
					} else if(null!=checkpointDetails.getFileConnectType() && checkpointDetails.getFileConnectType().equalsIgnoreCase(KeyValue.LOGMONITOR_CONNECT_TYPE)){  
						checkPointResult = fileMonitor.performCheck(checkpointDetails);
					} else if(null!=checkpointDetails.getSshConnectType() && checkpointDetails.getSshConnectType().equalsIgnoreCase(KeyValue.SSHWRAPPER_CONNECT_TYPE)){  
						checkPointResult = sshWrapper.performCheck(checkpointDetails);
					}else if(null!=checkpointDetails.getSnmpTableConnectType() && checkpointDetails.getSnmpTableConnectType().equalsIgnoreCase(KeyValue.SNMPTABLE_CONNECT_TYPE)){
						//Start:Added for SNMPTable
						checkPointResult = snmpTableMonitor.performCheck(checkpointDetails);
						//End:Added for SNMPTable
					}
					checkPointResult.setCheckPointDetails(checkpointDetails);
					checkPointResult.setExecutinTime(System.currentTimeMillis());//;getExecTimeinUTC());
					checkPointResult.setDowntimeScheduled(0);
					LOGGER.info("execution time "+System.currentTimeMillis());
				}
			}else{
				checkpointDetails.setCheckType('R');
				checkPointResult.setCheckPointDetails(checkpointDetails);
				checkPointResult.setCheckPointresult("Downtime Scheduled");
				checkPointResult.setDowntimeScheduled(1);
				checkPointResult.setExecutinTime(System.currentTimeMillis());//;getExecTimeinUTC());
				LOGGER.info("execution time "+System.currentTimeMillis());
				
			}
			
			LOGGER.info("execution time in ms  "+getExecTimeinUTC());
			LOGGER.info("execution time "+formatDate(getExecTimeinUTC()));
			
			LOGGER.info("device id "+checkpointDetails.getDeviceID()+" checkpoint id "+checkpointDetails.getCheckpointID());
			cacheManager.addToResultCache(checkPointResult);
			LOGGER.info("job is executing "+System.currentTimeMillis());
			//int i=1/0;
		} catch (Exception e) {
			boolean checkpointDetailsFlag = CommonUtilities.isNotNull(checkpointDetails);
			if(checkpointDetailsFlag){
				CommonUtilities.createErrorLogFile(new Exception("Checkpoint ID :: "+checkpointDetails.getCheckpointID()+"\n Checkpoint :: "+checkpointDetails.getCheckPoint(), e));
			}else {
				CommonUtilities.createErrorLogFile(e);
			}
		}
	}
	public String formatDate(long executionTime){
		  Date date=new Date(executionTime);
		  SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  return df2.format(date);
		 }
	private boolean checkTimeBasedDownTime(String startTime,String endTime, String startDate, String endDate) throws ParseException{
		boolean downTimeFlag = false;
		boolean dayFlag = false;
		try{
			boolean startDateFlag = CommonUtilities.isNotNullAndEmpty(startDate);
			boolean endDateFlag = CommonUtilities.isNotNullAndEmpty(endDate);
			if(startDateFlag && endDateFlag){
				dayFlag = isValidDay(startDate, endDate);
			} else {
				downTimeFlag = setStartDateAndEndDate(startTime,endTime);
			}
			if(dayFlag){
				downTimeFlag = setStartDateAndEndDate(startTime,endTime);
			}
		}catch(Exception e){
			LOGGER.error("Error while checking time based down time");
		}
		return downTimeFlag;

	}
	
	/*public boolean checkOverallDowntime(String startTime,String endTime,String fromDayOfWeek,String toDayOfWeek,String startDay,String endDay){
		
		return false;
	}
	
	public boolean checkDayBaseDowntime(String startTime,String endTime,String startDay,String endDay){
		String startDateTime = null;
		String endDateTime = null;
		boolean downTimeFlag = false;
		try{
			Calendar calendar = Calendar.getInstance();
			startDateTime = startDay+" "+startTime;
			endDateTime = endDay+" "+endTime;
			downTimeFlag = isDownTime(startDateTime,endDateTime);
		}catch(Exception e){
			LOGGER.error("Error while checking week based down time");
		}
		return downTimeFlag;
	}
*/
	private boolean checkWeekBasedDownTime(String startTime,String endTime,String startWeek,String endWeek, String startDate, String endDate) throws ParseException{
		String startDateTime = null;
		String endDateTime = null;
		Calendar calendar = null;
		boolean downTimeFlag = false;
		boolean dayFlag = false;
		try{
			boolean startDateFlag = CommonUtilities.isNotNullAndEmpty(startDate);
			boolean endDateFlag = CommonUtilities.isNotNullAndEmpty(endDate);
			if(startDateFlag && endDateFlag){
				dayFlag = isValidDay(startDate, endDate);
			} else {
				calendar = Calendar.getInstance();
				startDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE)+getStartDayOfWeek(startWeek));
				endDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE)+getEndDayOfWeek(endWeek));
				boolean dayflag1 = isValidDay(startDateTime, endDateTime);
				if (dayflag1) {
					downTimeFlag = setStartDateAndEndDate(startTime,endTime);
				}
			}
			if(dayFlag){
				calendar = Calendar.getInstance();
				startDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE)+getStartDayOfWeek(startWeek));
				endDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE)+getEndDayOfWeek(endWeek));
				boolean dayflag1 = isValidDay(startDateTime, endDateTime);
				if (dayflag1) {
					downTimeFlag = setStartDateAndEndDate(startTime,endTime);
				}
			} 
			
		}catch(Exception e){
			LOGGER.error("Error while checking week based down time");
		}
		return downTimeFlag;
	}
	
	private boolean setStartDateAndEndDate(String startTime, String endTime){
		String startDateTime = null;
		String endDateTime = null;
		Calendar calendar = null;
		boolean downTimeFlag = false;
		try{
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		Date cStartTime = timeFormatter.parse(startTime);
		Date cEndTime = timeFormatter.parse(endTime);
		calendar = Calendar.getInstance();
		startDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE))+" "+startTime;
		if (cEndTime.after(cStartTime)) { 
			endDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+(calendar.get(Calendar.DATE))+" "+endTime;
		} else if (cEndTime.before(cStartTime) || cEndTime.equals(cStartTime)) {
			endDateTime = ""+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) + 1)+ "-"+((calendar.get(Calendar.DATE))+1)+" "+endTime;
		}
		downTimeFlag = isDownTime(startDateTime,endDateTime);
		}catch(Exception e){
			LOGGER.error("Error while setting start date and end date");
		}
		return downTimeFlag;
		
	}
	
	private boolean isValidDay(String startDate, String endDate){
		boolean dayFlag = false;
		String formattedCurrentDate = null;
		Calendar calendar = Calendar.getInstance();
		try{
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(calendar.getTimeInMillis());
		formattedCurrentDate = dateFormatter.format(curDate);
		Date currentDate = dateFormatter.parse(formattedCurrentDate);
		Date parsedStartDate = dateFormatter.parse(startDate);
		Date parsedEndDate = dateFormatter.parse(endDate);
		
		if((currentDate.after(parsedStartDate) && currentDate.before(parsedEndDate)) || (currentDate.equals(parsedStartDate) || currentDate.equals(parsedEndDate))) {
			dayFlag = true;
		}
		}catch(Exception e){
			LOGGER.error("Error while checking if down time is valid on that day");
		}
		return dayFlag;
	}
	
	private boolean isDownTime(String startDateTime, String endDateTime){
		
		String currentDateTime = null;
		boolean downTimeFlag = false;
		Calendar calendar = Calendar.getInstance();
		try{
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date curDate = new Date(calendar.getTimeInMillis());
		currentDateTime = dateFormatter.format(curDate);
		Date currentDate = dateFormatter.parse(currentDateTime);
		Timestamp curTimestamp = new java.sql.Timestamp(currentDate.getTime());

		Date StartDate = dateFormatter.parse(startDateTime);
		Timestamp fromDateTime = new java.sql.Timestamp(StartDate.getTime());

		Date EndDate = dateFormatter.parse(endDateTime);
		Timestamp toDateTime = new java.sql.Timestamp(EndDate.getTime());

		if((curTimestamp.after(fromDateTime) && curTimestamp.before(toDateTime)) || (currentDate.equals(fromDateTime) || currentDate.equals(toDateTime))) {
			downTimeFlag = true;
		} 
		}catch(Exception e){
			LOGGER.error("Error while checking if down time is scheduled");
		}
		return downTimeFlag;
		
	}

	private int getStartDayOfWeek(String startweekDay){
		int startDay = 0; 
		try{
			Calendar mydate = Calendar.getInstance(); // and set this up however you need it.
			int today = mydate.get (Calendar.DAY_OF_WEEK);

			if(startweekDay.equalsIgnoreCase("MONDAY")){
				startDay=Calendar.MONDAY-today;
			} else if (startweekDay.equalsIgnoreCase("TUESDAY")){
				startDay=Calendar.TUESDAY-today;
			}else if (startweekDay.equalsIgnoreCase("WEDNESDAY")){
				startDay=Calendar.WEDNESDAY-today;
			}else if (startweekDay.equalsIgnoreCase("THURSDAY")){
				startDay=Calendar.THURSDAY-today;
			}else if (startweekDay.equalsIgnoreCase("FRIDAY")){
				startDay=Calendar.FRIDAY-today;
			}else if (startweekDay.equalsIgnoreCase("SATURDAY")){
				startDay=Calendar.SATURDAY-today;
			}else if (startweekDay.equalsIgnoreCase("SUNDAY")){
				startDay=Calendar.SUNDAY-today;
			}
		}catch(Exception e){
			LOGGER.error("Error while getting start day of week");
		}
		return startDay;
	}

	private int getEndDayOfWeek(String endWeekDay){
		int endDay = 0; 
		try{
			Calendar mydate = Calendar.getInstance(); // and set this up however you need it.
			int today = mydate.get (Calendar.DAY_OF_WEEK);

			if(endWeekDay.equalsIgnoreCase("MONDAY")){
				endDay=Calendar.MONDAY-today;
			} else if (endWeekDay.equalsIgnoreCase("TUESDAY")){
				endDay=Calendar.TUESDAY-today;
			}else if (endWeekDay.equalsIgnoreCase("WEDNESDAY")){
				endDay=Calendar.WEDNESDAY-today;
			}else if (endWeekDay.equalsIgnoreCase("THURSDAY")){
				endDay=Calendar.THURSDAY-today;
			}else if (endWeekDay.equalsIgnoreCase("FRIDAY")){
				endDay=Calendar.FRIDAY-today;
			}else if (endWeekDay.equalsIgnoreCase("SATURDAY")){
				endDay=Calendar.SATURDAY-today;
			}else if (endWeekDay.equalsIgnoreCase("SUNDAY")){
				endDay=Calendar.SUNDAY-today;
			}
		}catch(Exception e){
			LOGGER.error("Error while getting end day of week");
		}
		return endDay;
	}
	
	private long getExecTimeinUTC() {
	    Date utcDate = null;
		try {
			SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			utcDate = dateformat.parse(utcFormat.format(System.currentTimeMillis()));
		} catch (ParseException e) {
			LOGGER.error("",e);
		}
	   return utcDate.getTime();
	}

}
