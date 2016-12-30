package com.osi.manager.exception;


public class FinderException extends MSException {
	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public FinderException(String aCode, String aUserMessage, String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		
	}

	/**
	 * 
	 * @param aCode
	 * @param aUserMessage
	 * @param cause
	 */
	public FinderException(String aCode, String aUserMessage, Throwable cause) {
		super(aCode, aUserMessage, cause);
	}
	
}
