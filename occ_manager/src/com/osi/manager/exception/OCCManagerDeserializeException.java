/**
 * 
 */
package com.osi.manager.exception;

/**
 * @author jkorada1
 *
 */
public class OCCManagerDeserializeException extends OCCManagerException {

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCManagerDeserializeException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
	}

}
