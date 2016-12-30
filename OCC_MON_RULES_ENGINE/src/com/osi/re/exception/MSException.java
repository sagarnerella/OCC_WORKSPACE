package com.osi.re.exception;


public abstract class MSException extends Exception{
	
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
	public MSException(String aCode, String aUserMessage, String aSysMessage) {
		super(aSysMessage);
		this._code = aCode;
		this._userMessage = aUserMessage;
		
	}
	
	
	
	public MSException(String aCode, String aUserMessage, Throwable cause) {
		super(aUserMessage, cause);
		this._code = aCode;
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