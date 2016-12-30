package com.osi.manager.services;

import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.domain.CheckTestConnection;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.FinderException;

public class CheckTestConWaiter implements Runnable{
    
    private CheckTestConnection msg;
    IManagerDao iManagerDao = null;
    String result=null;
    public CheckTestConWaiter(CheckTestConnection m){
        this.msg=m;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        
        String result=null;
        synchronized (msg) {
            try{
            	String agentid=msg.getMsg();
                System.out.println(name+" waiting to get notified at time:"+System.currentTimeMillis());
                msg.wait();
                System.out.println("waiter notified :::::::::::::::");
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
            System.out.println(name+" waiter thread got notified at time:"+System.currentTimeMillis());
            //process the message now
            System.out.println(name+" processed: "+msg.getMsg());
           
        }
    }
    
    

}
