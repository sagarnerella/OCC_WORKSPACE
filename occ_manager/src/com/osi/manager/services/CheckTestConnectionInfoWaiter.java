package com.osi.manager.services;

import org.apache.log4j.Logger;

import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.domain.CheckTestConnectionInfo;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.FinderException;

public class CheckTestConnectionInfoWaiter implements Runnable{
	private static final Logger LOGGER = Logger.getLogger(CheckTestConnectionInfoWaiter.class);
    private CheckTestConnectionInfo checktestconinfo;
    IManagerDao iManagerDao = null;
    String result=null;
    public CheckTestConnectionInfoWaiter(CheckTestConnectionInfo checktestconinfo){
        this.checktestconinfo=checktestconinfo;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        LOGGER.info("CheckTestConnectionInfoWaiter :: run : start ");
        String result=null;
        synchronized (checktestconinfo) {
            try{
            	String agentid=checktestconinfo.getMsg();
            	LOGGER.info("Thread with name : "+name+" is  waiting to get notified at time :  "+System.currentTimeMillis());
            	
                checktestconinfo.wait();
                LOGGER.info("waiter notified :::::::::::::::");
                iManagerDao = (IManagerDao)new ManagerDao();
                 iManagerDao.SendTestConnResponse(agentid);
               
                
			 }catch(InterruptedException e){
                e.printStackTrace();
            }catch (FinderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            LOGGER.info("Thread with name : "+name+"  got notified at time : "+System.currentTimeMillis());
            
            LOGGER.info("Thread with name : "+name+" processed: "+checktestconinfo.getMsg());
           
        }
        
        LOGGER.info("CheckTestConnectionInfoWaiter :: run : end ");
    }
    
    

}
