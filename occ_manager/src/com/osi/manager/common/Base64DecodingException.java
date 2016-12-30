package com.osi.manager.common;

import java.io.IOException;

public class Base64DecodingException extends IOException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private char c;

    /**
     * Constructs a new exception.
     *
     * @param message later to be returned by a getMessage() call.
     * @param c character that caused this error.
     *
     */
    public Base64DecodingException(String message, char c)
    {
        super(message);
        this.c = c;
    }

    /**
     * Gets the character that caused this error.
     *
     * @return the character that caused this error.
     *
     */
    public char getChar()
    {
        return c;
    }
}
