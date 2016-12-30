package com.osi.re.exception;


public class WSException extends MSException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param aCode
	 * @param aUserMessage
	 * @param aSysMessage
	 */
	public WSException(String aCode, String aUserMessage, String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		// TODO Auto-generated constructor stub
	}

}
