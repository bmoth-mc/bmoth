package de.bmoth.app;

import java.util.prefs.Preferences;

/**
 * Created by Julian on 04.05.2017.
 */

public class PersonalPreferences {
    private static Preferences prefs = Preferences.userNodeForPackage(PersonalPreferences.class);
    private PersonalPreferences() {

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

    public static void setIntPreference(IntPreference p, String val) {
        prefs.put(p.toString(), val);
    }

    public enum StringPreference {
        LAST_FILE(""), LAST_DIR(System.getProperty("user.dir"));

        private String defaultValue;

        StringPreference(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }


    public enum IntPreference {
        MIN_INT(-1), MAX_INT(3), MAX_INITIAL_STATE(5), MAX_TRANSITIONS(5),Z3_TIMEOUT(5000);

        private int defaultValue;

        IntPreference(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public enum BooleanPreference {
        ;

        private boolean defaultValue;

        BooleanPreference(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
