package com.osi.manager.exception;

/**
 * @author jkorada
 *
 */

public abstract class OCCManagerException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The _code. */
	private String _code;
	
	/** The _user message. */
	private String _userMessage;

	/**
	 * @param aCode the a code
	 * @param aUserMessage the a user message
	 * @param aSysMessage the a sys message
	 */
	public OCCManagerException(String aCode, String aUserMessage, String aSysMessage) {
		super(aSysMessage);
		this._code = aCode;
		this._userMessage = aSysMessage;
	}

	/**
	 * Gets the code.
	 *
	 * @return the String
	 */
	public String getCode() {
		return _code;
	}

	/**
	 * Gets the user message.
	 *
	 * @return the String
	 */
	public String getUserMessage() {
		return _userMessage;
	}
}