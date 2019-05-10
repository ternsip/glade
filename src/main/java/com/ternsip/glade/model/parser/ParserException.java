package com.ternsip.glade.model.parser;

/**
 * Any errors from the parser is wrapped in a ParserException for
 * easy error handling.
 *
 * @author Kjetil �ster�s
 */
public class ParserException extends Exception {

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
