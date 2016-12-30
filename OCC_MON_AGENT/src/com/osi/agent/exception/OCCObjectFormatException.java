/**
 * 
 */
package com.osi.agent.exception;

/**
 * @author jkorada1
 *
 */
public class OCCObjectFormatException extends OCCAgentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
