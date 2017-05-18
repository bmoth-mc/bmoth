package de.bmoth.exceptions;

public class ErrorEvent {
    String errorType;
    String message;

    public ErrorEvent(String type, String msg) {
        errorType = type;
        message = msg;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorType() {
        return errorType;
    }
}
