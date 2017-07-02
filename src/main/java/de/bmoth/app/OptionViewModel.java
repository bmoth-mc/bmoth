package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.math.NumberUtils;

public class OptionViewModel implements ViewModel {
    private static final String NONNUMERICWARNING = "Not Numeric or out of Integer-Range: ";

    private SimpleStringProperty minInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
    private SimpleStringProperty maxInt = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
    private SimpleStringProperty maxInitState = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
    private SimpleStringProperty maxTrans = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
    private SimpleStringProperty z3Timeout = new SimpleStringProperty(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
    private SimpleStringProperty alertText = new SimpleStringProperty();

    void loadPrefs() {
        getMinInt().set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
        getMaxInt().set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
        getMaxInitState().set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
        getMaxTrans().set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
        getZ3Timeout().set(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
    }

    void savePrefs() {
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MIN_INT, Integer.parseInt(getMinInt().get()));
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INT, Integer.parseInt(getMaxInt().get()));
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE, Integer.parseInt(getMaxInitState().get()));
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS, Integer.parseInt(getMaxTrans().get()));
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT, Integer.parseInt(getZ3Timeout().get()));
    }

    public boolean checkPrefs() {
        if (!NumberUtils.isParsable(getMinInt().get())) {
            getAlertText().set(NONNUMERICWARNING + getMinInt().get());
            return false;
        }
        if (!NumberUtils.isParsable(getMaxInt().get())) {
            getAlertText().set(NONNUMERICWARNING + getMaxInt().get());
            return false;
        }
        if (!NumberUtils.isParsable(getMaxInitState().get())) {
            getAlertText().set(NONNUMERICWARNING + getMaxInitState().get());
            return false;
        }
        if (!NumberUtils.isParsable(getMaxTrans().get())) {
            getAlertText().set(NONNUMERICWARNING + getMaxTrans().get());
            return false;
        }
        if (!NumberUtils.isParsable(getZ3Timeout().get())) {
            getAlertText().set(NONNUMERICWARNING + getZ3Timeout().get());
            return false;
        }
        if (Integer.parseInt(getZ3Timeout().get()) < 0) {
            getAlertText().set("Timout needs to be a positive Value");
            return false;
        }
        if ((Integer.parseInt(getMinInt().get())) > Integer.parseInt(getMaxInt().get())) {
            getAlertText().set("MIN_INT bigger than MAX_INT");
            return false;
        }
        if (Integer.parseInt(getMaxInitState().get()) < 1) {
            getAlertText().set("InitialStates needs to be bigger than 0");
            return false;
        }
        if (Integer.parseInt(getMaxTrans().get()) < 1) {
            getAlertText().set("Maximum transitions needs to be bigger than 0");
            return false;
        }
        return true;
    }

    public SimpleStringProperty getMinInt() {
        return minInt;
    }

    public SimpleStringProperty getMaxInt() {
        return maxInt;
    }

    public SimpleStringProperty getMaxInitState() {
        return maxInitState;
    }

    public SimpleStringProperty getMaxTrans() {
        return maxTrans;
    }

    public SimpleStringProperty getZ3Timeout() {
        return z3Timeout;
    }

    public SimpleStringProperty getAlertText() {
        return alertText;
    }
}
