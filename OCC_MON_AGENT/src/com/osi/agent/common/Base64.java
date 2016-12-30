package com.osi.agent.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;


public class Base64
{
    private static final int END_OF_INPUT = -1;
    private static final int NON_BASE_64 = -1;

    /**
     * A character that is not a valid base 64 character.
     */
    private static final int NON_BASE_64_PADDING = -3;

    /**
     * A character that is not a valid base 64 character.
     */
    private static final int NON_BASE_64_WHITESPACE = -2;

    /**
     * Table of the sixty-four characters that are used as
     * the Base64 alphabet: [A-Za-z0-9+/]
     */
    protected static final byte[] base64Chars = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3',
        '4','5','6','7','8','9','+','/',
    };
    
    protected static final byte[] reverseBase64Chars = new byte[0x100];

    static
    {
        for (int i=0; i<reverseBase64Chars.length; i++)
        {
            reverseBase64Chars[i] = NON_BASE_64;
        }
        for (byte i=0; i < base64Chars.length; i++)
        {
            reverseBase64Chars[base64Chars[i]] = i;
        }
        reverseBase64Chars[' '] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\n'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\r'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\t'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\f'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['='] = NON_BASE_64_PADDING;
    }

    private Base64()
    {

    }

	/**
     * Encode a String in Base64.
     * The String is converted to and from bytes according to the platform's
     * default character encoding.
     * No line breaks or other white space are inserted into the encoded data.
     *
     * @param string The data to encode.
     * @return An encoded String.
     *

     */
    public static String encode(String string)
    {
        return new String(encode(string.getBytes()));
    }

/**
     * Encode bytes in Base64.
     * No line breaks or other white space are inserted into the encoded data.
     *
     * @param bytes The data to encode.
     * @return Encoded bytes.
     *
     */
    public static byte[] encode(byte[] bytes)
    {
        return encode(bytes, false);
    }

/**
     * Encode bytes in Base64.
     *
     * @param bytes The data to encode.
     * @param lineBreaks  Whether to insert line breaks every 76 characters in the output.
     * @return Encoded bytes.
     *
     */
    public static byte[] encode(byte[] bytes, boolean lineBreaks)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be 4/3 the size of the input
        // but the input length must be divisible by three.
        // If it isn't the next largest size that is divisible
        // by three is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 3) != 0)
        {
            length += 3 - mod;
        }
        length = length * 4 / 3;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try
        {
            encode(in, out, lineBreaks);
        }
        catch (IOException x)
        {
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
            throw new RuntimeException(x);
        }
        return out.toByteArray();
    }

/**
     * Encode data from the InputStream to the OutputStream in Base64.
     *
     * @param in Stream from which to read data that needs to be encoded.
     * @param out Stream to which to write encoded data.
     * @param lineBreaks Whether to insert line breaks every 76 characters in the output.
     * @throws IOException if there is a problem reading or writing.
     *
     */
    public static void encode(InputStream in, OutputStream out, boolean lineBreaks) throws IOException
    {
        // Base64 encoding converts three bytes of input to
        // four bytes of output
        int[] inBuffer = new int[3];
        int lineCount = 0;
        boolean done = false;
        while (!done && (inBuffer[0] = in.read()) != END_OF_INPUT)
        {
            // Fill the buffer
            inBuffer[1] = in.read();
            inBuffer[2] = in.read();

            // Calculate the out Buffer
            // The first byte of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not END_OF_INPUT before using them.
            // The basic idea is that the three bytes get split into
            // four bytes along these lines:
            //      [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            // bytes are considered to be zero when absent.
            // the four bytes are then mapped to common ASCII symbols

            // A's: first six bits of first byte
            out.write(base64Chars[ inBuffer[0] >> 2 ]);
            if (inBuffer[1] != END_OF_INPUT)
            {
                // B's: last two bits of first byte, first four bits of second byte
                out.write(base64Chars [(( inBuffer[0] << 4 ) & 0x30) | (inBuffer[1] >> 4) ]);
                if (inBuffer[2] != END_OF_INPUT)
                {
                    // C's: last four bits of second byte, first two bits of third byte
                    out.write(base64Chars [((inBuffer[1] << 2) & 0x3c) | (inBuffer[2] >> 6) ]);
                    // D's: last six bits of third byte
                    out.write(base64Chars [inBuffer[2] & 0x3F]);
                }
                else
                {
                    // C's: last four bits of second byte
                    out.write(base64Chars [((inBuffer[1] << 2) & 0x3c)]);
                    // an equals sign for a character that is not a Base64 character
                    out.write('=');
                    done = true;
                }
            }
            else
            {
                // B's: last two bits of first byte
                out.write(base64Chars [(( inBuffer[0] << 4 ) & 0x30)]);
                // an equal signs for characters that is not a Base64 characters
                out.write('=');
                out.write('=');
                done = true;
            }
            lineCount += 4;
            if (lineBreaks && lineCount >= 76)
            {
                out.write('\n');
                lineCount = 0;
            }
        }
        if (lineBreaks && lineCount >= 1)
        {
            out.write('\n');
            lineCount = 0;
        }
        out.flush();
    }

    /**
     * Reads the next (decoded) Base64 character from the input stream.
     * Non Base64 characters are skipped.
     *
     * @param in Stream from which bytes are read.
     * @param throwExceptions Throw an exception if an unexpected character is encountered.
     * @return the next Base64 character from the stream or -1 if there are no more Base64 characters on the stream.
     * @throws IOException if an IO Error occurs.
     * @throws Base64DecodingException if unexpected data is encountered when throwExceptions is specified.
     *
     */
    private static final int readBase64(InputStream in, boolean throwExceptions) throws IOException
    {
        int read;
        int numPadding = 0;
        do
        {
            read = in.read();
            if (read == END_OF_INPUT) return END_OF_INPUT;
            read = reverseBase64Chars[(byte)read];
            if (throwExceptions && (read == NON_BASE_64 || (numPadding > 0 && read > NON_BASE_64)))
            {
                throw new Base64DecodingException (
                    MessageFormat. format("unexpectedchar",
                        (Object[])new String[] {
                            "'" + (char)read + "' (0x" + Integer.toHexString(read) + ")"
                        }
                    ),
                    (char)read
                );
            }
            if (read == NON_BASE_64_PADDING)
            {
                numPadding++;
            }
        }
        while (read <= NON_BASE_64);
        return read;
    }

    /**
     * Decode a Base64 encoded String.
     * Characters that are not part of the Base64 alphabet are ignored
     * in the input.
     * The String is converted to and from bytes according to the platform's
     * default character encoding.
     *
     * @param string The data to decode.
     * @return A decoded String.
     *
     */
    public static String decode(String string)
    {
        return new String(decode(string.getBytes()));
    }

    /**
     * Decode Base64 encoded bytes.
     * Characters that are not part of the Base64 alphabet are ignored
     * in the input.
     *
     * @param bytes The data to decode.
     * @return Decoded bytes.
     *
     *
     */
    public static byte[] decode(byte[] bytes)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be at most 3/4 the size of the input
        // but the input length must be divisible by four.
        // If it isn't the next largest size that is divisible
        // by four is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 4) != 0)
        {
            length += 4 - mod;
        }
        length = length * 3 / 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try
        {
            decode(in, out, false);
        }
        catch (IOException x)
        {
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
             throw new RuntimeException(x);
        }
        return out.toByteArray();
    }

    /**
     * Decode Base64 encoded data from the InputStream to the OutputStream.
     * Characters in the Base64 alphabet, white space and equals sign are
     * expected to be in urlencoded data.  The presence of other characters
     * could be a sign that the data is corrupted.
     *
     * @param in Stream from which to read data that needs to be decoded.
     * @param out Stream to which to write decoded data.
     * @param throwExceptions Whether to throw exceptions when unexpected data is encountered.
     * @throws IOException if an IO error occurs.
     * @throws Base64DecodingException if unexpected data is encountered when throwExceptions is specified.
     *
     */
    
    public static void decode(InputStream in, OutputStream out, boolean throwExceptions) throws IOException 
    {
        // Base64 decoding converts four bytes of input to three bytes of output
        int[] inBuffer = new int[4];

        // read bytes unmapping them from their ASCII encoding in the process
        // we must read at least two bytes to be able to output anything
        boolean done = false;
        while (!done && (inBuffer[0] = readBase64(in, throwExceptions)) != END_OF_INPUT
            && (inBuffer[1] = readBase64(in, throwExceptions)) != END_OF_INPUT){
            // Fill the buffer
            inBuffer[2] = readBase64(in, throwExceptions);
            inBuffer[3] = readBase64(in, throwExceptions);

            // Calculate the output
            // The first two bytes of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not END_OF_INPUT before using them.
            // The basic idea is that the four bytes will get reconstituted
            // into three bytes along these lines:
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            //      [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // bytes are considered to be zero when absent.

            // six A and two B
            out.write(inBuffer[0] << 2 | inBuffer[1] >> 4);
            if (inBuffer[2] != END_OF_INPUT)
            {
                // four B and four C
                out.write(inBuffer[1] << 4 | inBuffer[2] >> 2);
                if (inBuffer[3] != END_OF_INPUT)
                {
                    // two C and six D
                    out.write(inBuffer[2] << 6 | inBuffer[3]);
                }
                else
                {
                    done = true;
                }
            }
            else
            {
                done = true;
            }
        }
        out.flush();
    }    
}
