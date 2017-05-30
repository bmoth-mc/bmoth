package de.bmoth.eventbus;

public class ErrorEvent {
    private String errorType;
    private String message;

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
