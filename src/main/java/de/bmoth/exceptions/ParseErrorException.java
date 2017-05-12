package de.bmoth.exceptions;

import org.antlr.v4.runtime.CommonToken;

public class ParseErrorException extends RuntimeException {
    private static final long serialVersionUID = 2305560853973886094L;

    public ParseErrorException(CommonToken token, String message) {
        super(message);
    }
}
