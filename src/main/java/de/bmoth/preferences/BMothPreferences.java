package de.bmoth.preferences;

public class BMothPreferences {
    private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(BMothPreferences.class);

    private BMothPreferences() {
        // should not be instantiated
    }

    public static String getStringPreference(StringPreference p) {
        return prefs.get(p.toString(), p.defaultValue);
    }

    public static void setStringPreference(StringPreference p, String val) {
        prefs.put(p.toString(), val);
    }

    public static boolean getBooleanPreference(BooleanPreference p) {
        return prefs.getBoolean(p.toString(), p.defaultValue);
    }

    public static void setBooleanPreference(BooleanPreference p, boolean val) {
        prefs.put(p.toString(), String.valueOf(val));
    }

    public static int getIntPreference(IntPreference p) {
        return prefs.getInt(p.toString(), p.defaultValue);
    }

    public static void setIntPreference(IntPreference p, int val) {
        prefs.put(p.toString(), String.valueOf(val));
    }

    public enum StringPreference {
        LAST_FILE(""), LAST_DIR(System.getProperty("user.dir"));

        private String defaultValue;

        StringPreference(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }


    public enum IntPreference {
        MIN_INT(-1), MAX_INT(3), MAX_INITIAL_STATE(5), MAX_TRANSITIONS(5), Z3_TIMEOUT(5000);

        private int defaultValue;

        IntPreference(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public enum BooleanPreference {
        INVARIANT_CHECK(true), MODEL_CHECK(true), INITIAL_CHECK(true);

        private boolean defaultValue;

        BooleanPreference(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
