package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by Julian on 09.06.2017.
 */
public class OptionViewModel implements ViewModel {
    private static final String NONNUMERICWARNING = "Not Numeric or out of Integer-Range: ";

    public SimpleStringProperty minInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
    public SimpleStringProperty maxInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
    public SimpleStringProperty maxInitState = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
    public SimpleStringProperty maxTrans = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
    public SimpleStringProperty z3Timeout = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
    public SimpleStringProperty alertText = new SimpleStringProperty();

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

    public boolean checkPrefs() {
        if (!NumberUtils.isParsable(minInt.get())) {
            alertText.set(NONNUMERICWARNING + minInt.get());
            return false;
        }
        if (!NumberUtils.isParsable(maxInt.get())) {
            alertText.set(NONNUMERICWARNING + maxInt.get());
            return false;
        }
        if (!NumberUtils.isParsable(maxInitState.get())) {
            alertText.set(NONNUMERICWARNING + maxInitState.get());
            return false;
        }
        if (!NumberUtils.isParsable(maxTrans.get())) {
            alertText.set(NONNUMERICWARNING + maxTrans.get());
            return false;
        }
        if (!NumberUtils.isParsable(z3Timeout.get())) {
            alertText.set(NONNUMERICWARNING + z3Timeout.get());
            return false;
        }
        if (Integer.parseInt(z3Timeout.get()) < 0) {
            alertText.set("Timout needs to be a positive Value");
            return false;
        }
        if ((Integer.parseInt(minInt.get())) > Integer.parseInt(maxInt.get())) {
            alertText.set("MIN_INT bigger than MAX_INT");
            return false;
        }
        if (Integer.parseInt(maxInitState.get()) < 1) {
            alertText.set("InitialStates needs to be bigger than 0");
            return false;
        }
        if (Integer.parseInt(maxTrans.get()) < 1) {
            alertText.set("Maximum transitions needs to be bigger than 0");
            return false;
        }
        return true;
    }
}
