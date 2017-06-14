package de.bmoth.eventbus;

public class ErrorEvent {
    private String errorType;
    private String message;
    private Exception exception;

    public ErrorEvent(String type, String msg) {
        errorType = type;
        message = msg;
    }

    public ErrorEvent(String type, String msg, Exception e) {
        errorType = type;
        message = msg;
        exception = e;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorType() {
        return errorType;
    }

    public Exception getException() {
        return this.exception;
    }
}
