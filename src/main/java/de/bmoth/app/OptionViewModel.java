package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Julian on 09.06.2017.
 */
public class OptionViewModel implements ViewModel {

    SimpleStringProperty minInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
    SimpleStringProperty maxInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
    SimpleStringProperty maxInitState = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
    SimpleStringProperty maxTrans = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
    SimpleStringProperty z3Timeout = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));

    void loadPrefs() {
        minInt.set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
        maxInt.set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
        maxInitState.set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
        maxTrans.set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
        z3Timeout.set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
    }

    void savePrefs() {
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MIN_INT, minInt.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INT, maxInt.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE, maxInitState.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS, maxTrans.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT, z3Timeout.get());
    }
}
