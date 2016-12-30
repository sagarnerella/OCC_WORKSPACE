package com.osi.manager.exception;

/**
 * The Class DBConnectException.
 * 
 * 
 */
public class DBConnectException extends OCCManagerException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9099248333843852325L;

	/**
	 * Instantiates a new dB connect com.osi.coregreen.exception.
	 * 
	 * @param aCode
	 *            the a code
	 * @param aUserMessage
	 *            the a user message
	 * @param aSysMessage
	 *            the a sys message
	 */
	public DBConnectException(String aCode, String aUserMessage,
			String aSysMessage) {
		super(aCode, aUserMessage, aSysMessage);
		// throw new UnsupportedOperationException();
	}

}