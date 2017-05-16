package de.bmoth.exceptions;

import de.bmoth.app.ExceptionReporter;
import javafx.scene.control.Alert;
import org.antlr.v4.runtime.CommonToken;

public class ParseErrorException extends RuntimeException {
    private static final long serialVersionUID = 2305560853973886094L;
    private CommonToken token;

    public ParseErrorException(CommonToken token, String message) {
        super(message);
        this.token = token;
        ExceptionReporter exceptionReporter = new ExceptionReporter(Alert.AlertType.ERROR,
            "A syntax error", toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error: Unexpected input '").append(token.getText()).append("' ");
        sb.append("in line ").append(token.getLine());
        sb.append(" column " + token.getCharPositionInLine()).append(".\n");
        sb.append("Additional information: ").append(super.getMessage());
        System.out.println(sb.toString());
        return sb.toString();
    }
}
