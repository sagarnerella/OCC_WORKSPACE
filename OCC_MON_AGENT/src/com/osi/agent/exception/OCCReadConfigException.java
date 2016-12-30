/**
 * 
 */
package com.osi.agent.exception;

/**
 * @author jkorada1
 *
 */
public class OCCReadConfigException extends OCCAgentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6138890781040433138L;

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCReadConfigException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);

	}

}
