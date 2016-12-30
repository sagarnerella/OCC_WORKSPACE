/**
 *
 */
package com.osi.manager.exception;


public class CreateException extends MSException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public CreateException(String aCode, String aUserMessage, String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		
	}

	/**
	 * 
	 * @param aCode
	 * @param aUserMessage
	 * @param cause
	 */
	public CreateException(String aCode, String aUserMessage, Throwable cause) {
		super(aCode, aUserMessage, cause);
	}
	
	
}
