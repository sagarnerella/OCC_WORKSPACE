/**
 * 
 */
package com.osi.agent.exception;

/**
 * @author jkorada1
 *
 */
public class OCCAgentSerializeException extends OCCAgentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OCCAgentSerializeException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
	}

}
