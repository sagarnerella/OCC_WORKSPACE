package com.osi.manager.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.domain.CheckTestConnectionInfo;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.FinderException;

public class CheckTestConnectionInfoNotifier implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(CheckTestConnectionInfoNotifier.class);
    private CheckTestConnectionInfo checktestconinfo;
    IManagerDao iManagerDao = null;
    public CheckTestConnectionInfoNotifier(CheckTestConnectionInfo checktestconinfo) {
        this.checktestconinfo = checktestconinfo;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        LOGGER.info("CheckTestConnectionInfoNotifier :: run : started  ..... ");
        LOGGER.info("Thread with name : "+name+" has started ..... ");
        try {
             iManagerDao = (IManagerDao)new ManagerDao();
            String result=null;
            String checkstatus=null;
            String checkresult=null;
            final long NANOSEC_PER_SEC = 1000*1000*1000;
             long CHECKTESTCONNECTION_ELAPSEDTIME =Long.parseLong(CommonUtilities.getProperty("CHECKTESTCONNECTION_ELAPSEDTIME"));
             long startTime = System.nanoTime();
            
            while((checkstatus==null || "N".equals(checkstatus)) && ((System.nanoTime()-startTime)< CHECKTESTCONNECTION_ELAPSEDTIME*NANOSEC_PER_SEC)){
            	Thread.sleep(10000);
            	result=iManagerDao.CheckTestConnResult(checktestconinfo);
            
            	LOGGER.info("CheckTestConnectionInfoNotifier :: checkresult ...  "+result);
            Map<String, String>  eleTypeMap = new HashMap<String, String>();
    		StringTokenizer token = new StringTokenizer(result, "&");
    		while(token.hasMoreElements()){
    		    String str = token.nextToken();
    		    StringTokenizer furtherToken = new StringTokenizer(str,"=");
    		    while(furtherToken.hasMoreTokens()){
    		        eleTypeMap.put(furtherToken.nextToken(), furtherToken.nextToken());
    		    }
    	   }
    		
    		checkstatus=eleTypeMap.get("checkstatus");
    		LOGGER.info("CheckTestConnectionInfoNotifier :: checkstatus .... "+checkstatus);
    	    checkresult =eleTypeMap.get("checkresult");
    	    LOGGER.info("CheckTestConnectionInfoNotifier :: checkresult .... "+checkresult);
        }
    		
    	  synchronized (checktestconinfo) {
                  
    		   if(checkstatus.equals("P")){
    			   LOGGER.info("CheckTestConnectionInfoNotifier ::   Waiter thread has notified : checkstatus : "+checkstatus);
    			   checktestconinfo.setCheckStatus(checkstatus);
        		   checktestconinfo.setCheckResult(checkresult);
                   checktestconinfo.notify();
                 }else{
                	 LOGGER.info("CheckTestConnectionInfoNotifier ::   Waiter thread has notified : checkstatus :: "+checkstatus);
                	 checkstatus="P";
                	 checkresult="Sorry, your request could not be processed. Please try again";
                	 checktestconinfo.setCheckStatus(checkstatus);
          		     checktestconinfo.setCheckResult(checkresult);
                	 checktestconinfo.notify();
                 }
    		   
    		   //checktestconinfo.setCheckStatus(checkstatus);
    		   //checktestconinfo.setCheckResult(checkresult);
             }
        } catch (InterruptedException | FinderException | CreateException e) {
        	LOGGER.error("CheckTestConnectionInfoNotifier :: got error1 :  ");
        	e.printStackTrace();
        }
        catch (Exception e) {
        	LOGGER.error("CheckTestConnectionInfoNotifier :: got error2 :  ");
            e.printStackTrace();
        }
        
        LOGGER.info("CheckTestConnectionInfoNotifier :: run : ended  ..... ");
        
    }
    
}