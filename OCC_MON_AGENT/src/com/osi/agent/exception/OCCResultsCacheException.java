/**
 * 
 */
package com.osi.agent.exception;

/**
 * @author jkorada1
 *
 */
public class OCCResultsCacheException extends OCCAgentException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 726596758343648487L;

	public OCCResultsCacheException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
	}

}
