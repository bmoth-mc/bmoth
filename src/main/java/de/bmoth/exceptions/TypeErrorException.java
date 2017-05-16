package de.bmoth.exceptions;

import de.bmoth.app.ExceptionReporter;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.types.Type;
import javafx.scene.control.Alert;

public class TypeErrorException extends RuntimeException {
    private static final long serialVersionUID = -5344167922965323221L;

    public TypeErrorException(Node node, String message) {
        super(message);
        ExceptionReporter exceptionReporter = new ExceptionReporter(Alert.AlertType.ERROR,
            "A type error", message);
    }

    public TypeErrorException(Node node, Type expected, Type found) {
        super(String.format("Expected %s but found %s.", expected.toString(), found.toString()));
        ExceptionReporter exceptionReporter = new ExceptionReporter(Alert.AlertType.ERROR,
            "A type error", String.format("Expected %s but found %s.", expected.toString(), found.toString()));
    }

}
