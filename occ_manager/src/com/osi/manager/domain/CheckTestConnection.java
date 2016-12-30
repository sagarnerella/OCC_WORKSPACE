package com.osi.manager.domain;

import java.io.Serializable;

public class CheckTestConnection implements Serializable{
/**
	 * 
	 */
private static final long serialVersionUID = 1L;
private String msg;
private String result;
private String checkstatus;
    
    public CheckTestConnection(String str){
        this.msg=str;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String str) {
        this.msg=str;
    }
    
    public String getCheckStatus(){
    	return checkstatus;
    }
    
    public void setCheckStatus(String checkstatus){
    	
    	this.checkstatus=checkstatus;
    }
    
    public String getResult(){
    	return result;
    } 
    
    public void setResult(String result){
    	this.result=result;
    }
    
     
    
    
    
}
