package com.osi.agent.exception;

public class OCCAgentLogException extends OCCAgentCheckpointMonitoringException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCAgentLogException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);

	}

}
