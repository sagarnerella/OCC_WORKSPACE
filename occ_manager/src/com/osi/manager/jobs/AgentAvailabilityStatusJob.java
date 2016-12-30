package com.osi.manager.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.config.constants.MSConstants;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.DBConnectException;
import com.osi.manager.exception.FinderException;
import com.osi.manager.exception.PersistException;
import com.osi.manager.exception.UpdateException;
import com.osi.manager.util.MSConnectionManager;
import com.osi.manager.vo.MSAgent;
import com.osi.manager.scheduler.MSScheduler;

public class AgentAvailabilityStatusJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(AgentAvailabilityStatusJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobGroupName="";
		String triggerGroupName="";
		@SuppressWarnings("unused")
		int agentId;
		int configuredtimeinminutes=Integer.parseInt(CommonUtilities.getProperty("PRECONFIGURED_TIME_IN_MINUTES"));
		jobGroupName = (String) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_JOB_GROUP_BASE_NAME"));
		triggerGroupName = (String) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("REPEAT_AGENT_AVAILABILITY_TRIGGER_GROUP_BASE_NAME"));
		agentId = (int) context.getJobDetail().getJobDataMap().get(CommonUtilities.getProperty("AGENT_ID"));
		
		Timestamp last_inserted_timestamp = null;
		Timestamp current_time=null;
		boolean flag = false;
		PreparedStatement pstimestamp=null;
		PreparedStatement psrepeatcount=null;
		Connection connection=null;	
		int time_difference_in_minutes=0;
		int repeatCount=0;
		LOGGER.info("AgentAvailabilityStatusJob :: execute : AgentAvailabilityStatusJob fired with agentid :: "+agentId);	
		LOGGER.info("AgentAvailabilityStatusJob :: execute : AgentAvailabilityStatusJob fired with agentid :: :::::::::::::::::::: "+agentId);
			
		try{
			 repeatCount=getRepeatCount(agentId);
			connection = MSConnectionManager.getConnection();
			if(connection!=null){
				pstimestamp=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_AGENTID_TIMESTAMP"));
				pstimestamp.setInt(1,agentId);
					ResultSet rs = pstimestamp.executeQuery();
					
					while(rs.next()){
						last_inserted_timestamp=rs.getTimestamp("last_inserted_time");
						  current_time=Timestamp.valueOf(formatDate1(getExecTimeinUTC()));
					}
					
					        
					        LOGGER.info("AgentAvailabilityStatusJob :: execute : current_time :  "+current_time+" last_inserted_time : "+last_inserted_timestamp);
					        if( last_inserted_timestamp == null) {
					        	time_difference_in_minutes=0;
					        }else{
					        	time_difference_in_minutes=(int)((current_time.getTime()/(60 * 1000)) - (last_inserted_timestamp.getTime()/ (60 * 1000)));
					        }
						            
						            LOGGER.info(" AgentAvailabilityStatusJob :: time_difference_in_minutes :: "+time_difference_in_minutes);
						            
									// if(time_difference_in_minutes>=5){
						            if(time_difference_in_minutes>=configuredtimeinminutes){
										LOGGER.info("AgentAvailabilityStatusJob :: time_difference_in_minutes>=configuredtimeinminutes : "+time_difference_in_minutes);	
										repeatCount=repeatCount+1;
										if(repeatCount>=3){
											LOGGER.info("AgentAvailabilityStatusJob :: repeatCount "+repeatCount);	
											psrepeatcount=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_AUDIT_POLL"));
											psrepeatcount.setInt(1,agentId);
											psrepeatcount.setString(2,"Z");
											psrepeatcount.setInt(3,1);
											psrepeatcount.setTimestamp(4,Timestamp.valueOf(formatDate1(getExecTimeinUTC())));
											psrepeatcount.setInt(5,1);
											psrepeatcount.setTimestamp(6,Timestamp.valueOf(formatDate1(getExecTimeinUTC())));
											psrepeatcount.setString(7,"AGENT_UNAVAILABLE");
											psrepeatcount.executeUpdate();
											psrepeatcount.close();
											repeatCount=0;
											LOGGER.info(" AgentAvailabilityStatusJob :: inserted  agent details in ms_agent_poll_audit table with status 'Z' ");
											updateRepeatCount(agentId,repeatCount);
											
										}else{
											updateRepeatCount(agentId,repeatCount);
										}
										
									}else{
										 repeatCount=0;
										 updateRepeatCount(agentId,repeatCount);
											
								}
						            
			
		 }
			LOGGER.info("AgentAvailabilityStatusJob :: execute : AgentAvailabilityStatusJob completed ");	
				
		}catch (Exception e) {
			LOGGER.error("AgentAvailabilityStatusJob :: execute : got exception : ");
			e.printStackTrace();
			} finally{
						if(pstimestamp!=null){
							try {
								pstimestamp.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}if(psrepeatcount!=null){
							try {
								psrepeatcount.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
							if(connection!=null){
								try {
									connection.commit();
									connection.close();
								} catch (SQLException e) {
									LOGGER.error("",e);
									
								}
							}
							
			  }
		
	}
	
	
	private int getRepeatCount(int agentId) throws CreateException,FinderException{
    	PreparedStatement preparedStatement=null;
		Connection connection=null;	
		ResultSet rs=null;
		//IManagerDao iManagerDao = (IManagerDao)new ManagerDao();
		int repeat_count = 0;
		try{
			
			connection = MSConnectionManager.getConnection();
							if(connection!=null){
								preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_AGENTID_REPEATCOUNT"));
							    preparedStatement.setInt(1,agentId);
							    rs = preparedStatement.executeQuery();
							    while(rs.next()){
							    	repeat_count=rs.getInt("repeat_count");
								     
							}
							    LOGGER.info("AgentAvailabilityStatusJob :: getRepeatCount : Select repeat count for agentid : "+agentId);
			} 
		}
		catch (Exception e) {
			throw (CreateException)handleException(e, "select", "Unable to execute getRepeatCount select query", "Error occured while selecting repeatcount ", null);
		} finally{
				if(preparedStatement!=null){
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			closeDBConnection(connection, null, rs);
		}
		LOGGER.info("AgentAvailabilityStatusJob :: getRepeatCount : repeat_count : "+repeat_count);
		return repeat_count;
}
	
	
	
	  private void updateRepeatCount(int agentId, int repeatCount) throws CreateException,FinderException{
	    	PreparedStatement preparedStatement=null;
			Connection connection=null;	
			IManagerDao iManagerDao = (IManagerDao)new ManagerDao();
			try{
				LOGGER.info("AgentAvailabilityStatusJob :: updateRepeatCount : repeatCount .....  "+repeatCount);
				connection = MSConnectionManager.getConnection();
								if(connection!=null){
									preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_UPDATE_HEARTBEAT_REPEATCOUNT"));
									preparedStatement.setInt(1,repeatCount);
									preparedStatement.setInt(2,agentId);
									preparedStatement.executeUpdate();
									preparedStatement.close();
									LOGGER.info(" AgentAvailabilityStatusJob :: updateRepeatCount : updatedRepeatCount :: ---> "+repeatCount);
					
				} 
			}
			catch (Exception e) {
				throw (CreateException)handleException(e, "Create", "Unable to  updateRepeatCount in ms_agent_hearbeat table", "Error occured while  updateRepeatCount in ms_agent_hearbeat table ", null);
			} finally{
				closeDBConnection(connection, null, null);
			}
	}
	  
	  
	  private Exception handleException(Exception e, String exceptionType, String sqlExceptionMessage, String globalExceptionMessage, Connection connection){
			Exception exception=null;
			if("Finder".equalsIgnoreCase(exceptionType)){
				if(e instanceof DBConnectException){
					DBConnectException dbException=(DBConnectException)e;
					exception=new FinderException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
				}else if(e instanceof SQLException){
					exception=new FinderException(MSConstants.ERROR_CODE_7002, sqlExceptionMessage, e);
				}else if(e instanceof FinderException){
					FinderException fException=(FinderException)e;
					exception=new FinderException(fException.getCode(), fException.getUserMessage(), e);
				}else{
					exception=new FinderException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
				}
			}else if("Create".equalsIgnoreCase(exceptionType)){
				if(e instanceof DBConnectException){
					DBConnectException dbException=(DBConnectException)e;
					exception=new CreateException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
				}else if(e instanceof PersistException){
					PersistException pException=(PersistException)e;
					exception=new CreateException(pException.getCode(), pException.getUserMessage(), e);
					try{
						connection.rollback();
					}catch (Exception e1) {
						exception=new CreateException(MSConstants.ERROR_CODE_7002, "Unable to rollback", e1.getMessage()+"::"+e.getMessage());
					}
				}else{
					exception=new CreateException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
				}
			}else if("Update".equalsIgnoreCase(exceptionType)){
				if(e instanceof DBConnectException){
					DBConnectException dbException=(DBConnectException)e;
					exception=new UpdateException(dbException.getCode(), dbException.getUserMessage(), dbException.getMessage());
				}else if(e instanceof PersistException){
					PersistException pException=(PersistException)e;
					exception=new UpdateException(pException.getCode(), pException.getUserMessage(), e);
					try{
						connection.rollback();
					}catch (Exception e1) {
						exception=new UpdateException(MSConstants.ERROR_CODE_7002, "Unable to rollback", e1.getMessage()+"::"+e.getMessage());
					}
				}else{
					exception=new UpdateException(MSConstants.ERROR_CODE_7001, globalExceptionMessage, e);
				}
			}else if("Persist".equalsIgnoreCase(exceptionType)){
				exception=new PersistException(MSConstants.ERROR_CODE_7002, globalExceptionMessage, e.getMessage());
			}
			return exception;
		}
	    
	    
		private void closeDBConnection(Connection connection, Statement statement, ResultSet resultSet){
			if(resultSet!=null){
				try {
					resultSet.close();
				} catch (SQLException e) {
					LOGGER.error("",e);
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("",e);
				}
			}
			if(connection!=null){
				try {
					connection.commit();
					connection.close();
				} catch (SQLException e) {
					LOGGER.error("",e);
				}
				connection=null;
			}
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
		
		private String formatDate1(long executionTime){
			Date date=new Date(executionTime);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df2.format(date);
		}
	  
}

