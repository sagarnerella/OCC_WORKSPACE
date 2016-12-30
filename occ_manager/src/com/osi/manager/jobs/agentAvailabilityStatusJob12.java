package com.osi.manager.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.osi.manager.vo.MSAgent;
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

public class agentAvailabilityStatusJob12 implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(agentAvailabilityStatusJob12.class);
	private int agentId;
    
    public agentAvailabilityStatusJob12(int agentId){
        this.agentId=agentId;
    }
 
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" agentId : "+agentId+" Start. Time = "+new Date());
        try {
        	processJob();
		} catch (CreateException | FinderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(Thread.currentThread().getName()+" agentId : "+agentId+" End. Time = "+new Date());
    }
 
    private void processJob() throws CreateException,FinderException{
    	java.util.Date date= new java.util.Date();
    	Timestamp timestamp = null;
    	PreparedStatement preparedStatement=null;
		Connection connection=null;	
		int noofDaysinminutes=0;
		IManagerDao iManagerDao = (IManagerDao)new ManagerDao();
		
		try{
			
			List<MSAgent> msAgentsList = iManagerDao.getAgentsList();
			
			for (MSAgent msAgent : msAgentsList) {
				LOGGER.info("Scheduled Jobs for Agent :: ---> "+msAgent.getAgentId());
			
			
							connection = MSConnectionManager.getConnection();
							if(connection!=null){
								preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_SELECT_AGENT_TIMESTAMP"));
								preparedStatement.setInt(1,msAgent.getAgentId());
								ResultSet rs = preparedStatement.executeQuery();
								while(rs.next()){
									 timestamp=rs.getTimestamp("last_inserted_time");
									 System.out.println("last_inserted_timestamp ::: "+timestamp);
								}
								
						       
						        Timestamp current_time=new Timestamp(date.getTime());
						        System.out.println("current_time :: "+current_time+" timestamp :: "+timestamp);
						        if( timestamp == null || current_time == null ) {
						        	noofDaysinminutes=0;
						        }
					
						            noofDaysinminutes=(int)((current_time.getTime()/ (60 * 1000)) - (timestamp.getTime()/ (60 * 1000)));
						            System.out.println(" noofDaysinminutes :: "+noofDaysinminutes);
							} 
		
								System.out.println("insert agent details in to agent_audit_poll  table");
				preparedStatement=connection.prepareStatement(CommonUtilities.getProperty("QUERY_INSERT_AUDIT_POLL"));
				//preparedStatement.setString(1,agentId);
				preparedStatement.setInt(1,msAgent.getAgentId());
				if(noofDaysinminutes>=5){
					 preparedStatement.setString(2,"Z");
				}else{
					 preparedStatement.setString(2,"P");
				}
		
				preparedStatement.setInt(3,1);
				preparedStatement.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
				preparedStatement.setInt(5,1);
				preparedStatement.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
				preparedStatement.execute();
				connection.commit();
				System.out.println("inserted  agent details in audit poll table");
			}
		} catch (Exception e) {
			throw (CreateException)handleException(e, "Create", "Unable to execute saveDeviceXml insert query", "Error occured while saving saveDeviceXml ", null);
		} finally{
			closeDBConnection(connection, preparedStatement, null);
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
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("",e);
			}
			connection=null;
		}
	}
 
  }
