package de.bmoth.parser;

public class ParserException extends Exception {
    private static final long serialVersionUID = -1087348864429528237L;
    private final Exception exception;

    public ParserException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return this.exception;
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
