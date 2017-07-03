package de.bmoth.backend.translator;

import de.bmoth.backend.TranslationOptions;
import org.junit.Test;

import static de.bmoth.backend.TranslationOptions.PRIMED_0;
import static de.bmoth.backend.TranslationOptions.UNPRIMED;
import static org.junit.Assert.assertEquals;

public class TranslationOptionTest {
    @Test
    public void testToString() {
        TranslationOptions ops = new TranslationOptions(234);
        assertEquals("prime level 234", ops.toString());
        assertEquals("prime level 0", PRIMED_0.toString());
        assertEquals("not primed", UNPRIMED.toString());
    }
}
