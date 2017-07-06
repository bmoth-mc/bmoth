package de.bmoth.eventbus;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventBusProviderTest {

    @Test
    public void testEventBusProvider() {
        EventBusProvider instance1 = EventBusProvider.getInstance();
        EventBusProvider instance2 = EventBusProvider.getInstance();

        assertNotNull(instance1);
        assertTrue(instance1 == instance2);
        assertNotNull(instance1.getEventBus());
    }
}
