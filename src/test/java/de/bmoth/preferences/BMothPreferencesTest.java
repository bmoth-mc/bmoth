package de.bmoth.preferences;

import de.bmoth.preferences.BMothPreferences;
import org.junit.Test;

import static de.bmoth.preferences.BMothPreferences.BooleanPreference.INITIAL_CHECK;
import static de.bmoth.preferences.BMothPreferences.BooleanPreference.INVARIANT_CHECK;
import static de.bmoth.preferences.BMothPreferences.BooleanPreference.MODEL_CHECK;
import static de.bmoth.preferences.BMothPreferences.IntPreference.*;
import static de.bmoth.preferences.BMothPreferences.StringPreference.LAST_DIR;
import static de.bmoth.preferences.BMothPreferences.StringPreference.LAST_FILE;
import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class BMothPreferencesTest {

     @Test
    public void testIntPreference() {
        assertEquals(MIN_INT, BMothPreferences.IntPreference.valueOf("MIN_INT"));
        assertEquals(MAX_INT, BMothPreferences.IntPreference.valueOf("MAX_INT"));
        assertEquals(MAX_INITIAL_STATE, BMothPreferences.IntPreference.valueOf("MAX_INITIAL_STATE"));
        assertEquals(MAX_TRANSITIONS, BMothPreferences.IntPreference.valueOf("MAX_TRANSITIONS"));
        assertEquals(Z3_TIMEOUT, BMothPreferences.IntPreference.valueOf("Z3_TIMEOUT"));

        assertArrayEquals(new BMothPreferences.IntPreference[]{MIN_INT, MAX_INT, MAX_INITIAL_STATE, MAX_TRANSITIONS, Z3_TIMEOUT}, BMothPreferences.IntPreference.values());
    }

    @Test
    public void testStringPreference() {
        assertEquals(LAST_FILE, BMothPreferences.StringPreference.valueOf("LAST_FILE"));
        assertEquals(LAST_DIR, BMothPreferences.StringPreference.valueOf("LAST_DIR"));

        assertArrayEquals(new BMothPreferences.StringPreference[]{LAST_FILE, LAST_DIR}, BMothPreferences.StringPreference.values());

        BMothPreferences.setStringPreference(LAST_FILE, "test.mch");
        assertEquals("test.mch", BMothPreferences.getStringPreference(LAST_FILE));

        BMothPreferences.setStringPreference(LAST_FILE, "");
        assertEquals("", BMothPreferences.getStringPreference(LAST_FILE));
    }

    @Test
    public void testBooleanPreference() {
        assertEquals(INVARIANT_CHECK, BMothPreferences.BooleanPreference.valueOf("INVARIANT_CHECK"));
        assertEquals(MODEL_CHECK, BMothPreferences.BooleanPreference.valueOf("MODEL_CHECK"));
        assertEquals(INITIAL_CHECK, BMothPreferences.BooleanPreference.valueOf("INITIAL_CHECK"));

        assertArrayEquals(new BMothPreferences.BooleanPreference[]{INVARIANT_CHECK, MODEL_CHECK, INITIAL_CHECK}, BMothPreferences.BooleanPreference.values());

        BMothPreferences.setBooleanPreference(INVARIANT_CHECK, false);
        assertFalse(BMothPreferences.getBooleanPreference(INVARIANT_CHECK));

        BMothPreferences.setBooleanPreference(INVARIANT_CHECK, true);
        assertTrue(BMothPreferences.getBooleanPreference(INVARIANT_CHECK));
    }

    @Test
    public void testClass() {
        assertUtilityClassWellDefined(BMothPreferences.class);
    }

}
