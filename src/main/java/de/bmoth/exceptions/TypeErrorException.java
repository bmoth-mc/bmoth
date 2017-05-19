package de.bmoth.exceptions;

import com.google.common.eventbus.EventBus;
import de.bmoth.app.EventBusProvider;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.types.Type;

public class TypeErrorException extends RuntimeException {
    private static final long serialVersionUID = -5344167922965323221L;

    public TypeErrorException(Node node, String message) {
        super(message);
        EventBus eventBus = EventBusProvider.getInstance().getEventBus();
        eventBus.post(new ErrorEvent("Type error", toString()));
    }

    public TypeErrorException(Node node, Type expected, Type found) {
        super(String.format("Expected %s but found %s.", expected.toString(), found.toString()));
        EventBus eventBus = EventBusProvider.getInstance().getEventBus();
        eventBus.post(new ErrorEvent("Type error", String.format("Expected %s but found %s.",
            expected.toString(), found.toString())));
    }

}
