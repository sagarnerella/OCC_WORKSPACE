/**
 * 
 */
package com.osi.agent.exception;

/**
 * @author jkorada1
 *
 */
public class OCCPDUCreationException extends OCCAgentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
