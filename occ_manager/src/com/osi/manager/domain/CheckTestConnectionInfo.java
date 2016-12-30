package com.osi.manager.domain;

import java.io.Serializable;

public class CheckTestConnectionInfo implements Serializable{
/**
	 * 
	 */
private static final long serialVersionUID = 1L;
private String checktestconinfo;
private String checkresult;
private String checkstatus;
    
    public CheckTestConnectionInfo(String checktestconinfo){
        this.checktestconinfo=checktestconinfo;
    }

    public String getMsg() {
        return checktestconinfo;
    }

    public void setMsg(String str) {
        this.checktestconinfo=str;
    }
    
    public String getCheckStatus(){
    	return checkstatus;
    }
    
    public void setCheckStatus(String checkstatus){
    	
    	this.checkstatus=checkstatus;
    }
    
    public String getCheckResult(){
    	return checkresult;
    } 
    
    public void setCheckResult(String checkresult){
    	this.checkresult=checkresult;
    }
    
     
    
    
    
}
