package de.bmoth.exceptions;

public class ErrorEvent {
    private String errorType;
    private String message;

    ErrorEvent(String type, String msg) {
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
