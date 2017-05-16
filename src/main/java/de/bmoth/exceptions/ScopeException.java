package de.bmoth.exceptions;

import de.bmoth.app.ExceptionReporter;
import javafx.scene.control.Alert;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ScopeException extends RuntimeException {
    private static final long serialVersionUID = 6584928829237049955L;

    public ScopeException(ParserRuleContext ctx, String message) {
        super(message);
        ExceptionReporter exceptionReporter = new ExceptionReporter(Alert.AlertType.ERROR,
            "A scope exception", message);
    }

    public ScopeException(Token identifierToken, String message) {
        super(message);
        ExceptionReporter exceptionReporter = new ExceptionReporter(Alert.AlertType.ERROR,
            "A scope exception", message);
    }
}
