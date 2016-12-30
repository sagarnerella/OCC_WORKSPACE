package com.osi.agent.exception;

public class OCCAgentJDBCException extends OCCAgentCheckpointMonitoringException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public OCCAgentJDBCException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		// TODO Auto-generated constructor stub
	}

}
