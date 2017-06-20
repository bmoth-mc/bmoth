package de.bmoth.eventbus;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorEventTest {

    private static final String TEST_EXCEPTION =  "TEST_EXCEPTION";
    private static final String TEST_MESSAGE =  "TEST_MESSAGE";
    private static final String TEST_TYPE =  "TEST_TYPE";

    @Test
    public void errorEventTest() {
        Exception testException = new Exception(TEST_EXCEPTION);
        ErrorEvent event1 = new ErrorEvent(TEST_TYPE, TEST_MESSAGE);
        ErrorEvent event2 = new ErrorEvent(TEST_TYPE, TEST_MESSAGE, testException);
        assertEquals(TEST_TYPE, event1.getErrorType());
        assertEquals(TEST_MESSAGE, event1.getMessage());
        assertEquals(TEST_TYPE, event2.getErrorType());
        assertEquals(TEST_MESSAGE, event2.getMessage());
        assertEquals(testException, event2.getException());
    }

}
