package de.bmoth.app;

import java.util.prefs.Preferences;

/**
 * Created by Julian on 04.05.2017.
 */

public class PersonalPreferences {
    public enum StringPreference {
        LAST_FILE(""), LAST_DIR(System.getProperty("user.dir"));

        private String defaultValue;

        StringPreference(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public enum IntPreference {
        MIN_INT(-1), MAX_INT(3), MAX_INITIAL_STATE(5), MAX_TRANSITIONS(5);

        private int defaultValue;

        IntPreference(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    private static Preferences prefs = Preferences.userNodeForPackage(PersonalPreferences.class);

    private int minINT;
    private int maxINT;
    private int maxInitialStates = 5;
    private int maxSolution = 5;

    private PersonalPreferences() {

    }

    public static String getStringPreference(StringPreference p) {
        return prefs.get(p.toString(), p.defaultValue);
    }

    public static void setStringPreference(StringPreference p, String val) {

        prefs.put(p.toString(), val);
    }

    public static int getIntPreference(IntPreference p) {
        return prefs.getInt(p.toString(), p.defaultValue);
    }

    public static void setIntPreference(IntPreference p, String val) {

        prefs.put(p.toString(), val);
    }
}
