/**
 * 
 */
package com.osi.manager.exception;

/**
 * @author jkorada1
 *
 */
public class OCCObjectFormatException extends OCCManagerException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCObjectFormatException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
	}

}
