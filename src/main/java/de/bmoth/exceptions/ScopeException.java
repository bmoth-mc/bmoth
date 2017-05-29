package de.bmoth.exceptions;

import com.google.common.eventbus.EventBus;
import de.bmoth.eventbus.EventBusProvider;

public class ScopeException extends RuntimeException {
    private static final long serialVersionUID = 6584928829237049955L;

    public ScopeException(String message) {
        super(message);
        EventBus eventBus = EventBusProvider.getInstance().getEventBus();
        eventBus.post(new ErrorEvent("Scope exception", message));
    }
}
