package com.osi.agent.exception;

public class OCCAgentSSHException extends OCCAgentCheckpointMonitoringException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCAgentSSHException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);

	}

}
