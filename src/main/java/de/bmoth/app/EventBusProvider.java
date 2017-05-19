package de.bmoth.app;

import com.google.common.eventbus.EventBus;

public class EventBusProvider {

    private static EventBusProvider eventBusProvider = null;
    private final EventBus eventBus;

    private EventBusProvider() {
        eventBus = new EventBus();
    }

    public static EventBusProvider getInstance() {
        if (eventBusProvider == null) {
            eventBusProvider = new EventBusProvider();
        }
        return eventBusProvider;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
