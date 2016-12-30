/**
 *
 */
package com.osi.manager.exception;

public class UpdateException extends MSException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public UpdateException(String aCode, String aUserMessage, String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		
	}

	/**
	 * 
	 * @param aCode
	 * @param aUserMessage
	 * @param cause
	 */
	public UpdateException(String aCode, String aUserMessage, Throwable cause) {
		super(aCode, aUserMessage, cause);
	}

}
