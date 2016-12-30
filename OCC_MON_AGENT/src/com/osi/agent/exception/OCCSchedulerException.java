package com.osi.agent.exception;


public class OCCSchedulerException extends RuntimeException{
	
	private String userMessage;
	
	public OCCSchedulerException(String userMessage){
		
		this.userMessage= userMessage;
	}

	

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	
	
	
}
