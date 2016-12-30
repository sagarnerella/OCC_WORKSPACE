/**
 * 
 */
package com.osi.manager.exception;

/**
 * @author jkorada1
 *
 */
public class OCCManagerSNMPException extends OCCManagerException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCManagerSNMPException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		
	}

}
