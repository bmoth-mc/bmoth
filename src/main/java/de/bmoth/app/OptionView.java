package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Julian on 09.06.2017.
 */
public class OptionView implements FxmlView<OptionViewModel>, Initializable {

    private static final String NONNUMERICWARNING = "Not Numeric or out of Integer-Range: ";
    @InjectViewModel
    OptionViewModel optionViewModel;
    @FXML
    Button cancelButton;
    @FXML
    Button okButton;
    @FXML
    Button applyButton;
    @FXML
    TextField minInt;
    @FXML
    TextField maxInt;
    @FXML
    TextField maxInitState;
    @FXML
    TextField maxTrans;
    @FXML
    TextField z3Timeout;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minInt.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
        maxInt.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
        maxInitState.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
        maxTrans.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
        z3Timeout.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
        optionViewModel.minInt.bind(minInt.textProperty());
        optionViewModel.maxInt.bind(maxInt.textProperty());
        optionViewModel.maxInitState.bind(maxInitState.textProperty());
        optionViewModel.maxTrans.bind(maxTrans.textProperty());
        optionViewModel.z3Timeout.bind(z3Timeout.textProperty());

    }

    boolean checkPrefs() {
        if (!NumberUtils.isParsable(optionViewModel.minInt.get())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + minInt.getId()).show();
            return false;
        }
        if (!NumberUtils.isParsable(optionViewModel.maxInt.get())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxInt.getId()).show();
            return false;
        }
        if (!NumberUtils.isParsable(optionViewModel.maxInitState.get())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxInitState.getId()).show();
            return false;
        }
        if (!NumberUtils.isParsable(optionViewModel.maxTrans.get())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxTrans.getId()).show();
            return false;
        }
        if (!NumberUtils.isParsable(optionViewModel.z3Timeout.get())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + z3Timeout.getId()).show();
            return false;
        }

        if (Integer.parseInt(optionViewModel.z3Timeout.get()) < 0) {
            new Alert(Alert.AlertType.ERROR, "Timout needs to be a positive Value").show();
            return false;
        }
        if ((Integer.parseInt(optionViewModel.minInt.get())) > Integer.parseInt(optionViewModel.maxInt.get())) {
            new Alert(Alert.AlertType.ERROR, "MIN_INT bigger than MAX_INT").show();
            return false;
        }
        if (Integer.parseInt(optionViewModel.maxInitState.get()) < 1) {
            new Alert(Alert.AlertType.ERROR, "InitialStates needs to be bigger than 0").show();
            return false;
        }
        if (Integer.parseInt(optionViewModel.maxTrans.get()) < 1) {
            new Alert(Alert.AlertType.ERROR, "Maximum transitions needs to be bigger than 0").show();
            return false;
        }

        return true;
    }

    void savePrefs() {
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MIN_INT, optionViewModel.minInt.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INT, optionViewModel.maxInt.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE, optionViewModel.maxInitState.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS, optionViewModel.maxTrans.get());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT, optionViewModel.z3Timeout.get());
    }


    public void handleApply() {
        if (checkPrefs())
            savePrefs();
    }

    public void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void handleOk() {
        if (checkPrefs()) {
            savePrefs();
            handleClose();
        }
    }

}
