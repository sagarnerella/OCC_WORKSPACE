/**
 * 
 */
package com.osi.manager.exception;

/**
 * @author jkorada1
 *
 */
public class OCCPDUCreationException extends OCCManagerException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCPDUCreationException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
	}

}
