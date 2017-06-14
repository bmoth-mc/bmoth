package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by julian on 14.06.17.
 */
public class CustomCheckView implements FxmlView<CustomCheckViewModel>, Initializable {

    @InjectViewModel
    CustomCheckViewModel customCheckViewModel;
    @FXML
    CheckBox initialCheck;
    @FXML
    CheckBox invariantCheck;
    @FXML
    CheckBox modelCheck;
    AppView appView;

    private void savePrefs() {
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK, initialCheck.isSelected());
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK, invariantCheck.isSelected());
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK, modelCheck.isSelected());
    }

    public void handleClose() {
        ((Stage) initialCheck.getScene().getWindow()).close();
    }

    public void handleOk() {
        savePrefs();
        ((Stage) initialCheck.getScene().getWindow()).close();
        if (this.initialCheck.isSelected()) {
            appView.handleInitialStateExists();
        }
        if (this.invariantCheck.isSelected()) {
            appView.handleInvariantSatisfiability();
        }
        if (this.modelCheck.isSelected()) {
            appView.handleCheck();
        }
    }


    public void setAppControllerReference(AppView appView) {
        this.appView = appView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customCheckViewModel.checkInitProperty().bind(initialCheck.selectedProperty());
        customCheckViewModel.checkInvarProperty().bind(invariantCheck.selectedProperty());
        customCheckViewModel.checkModelProperty().bind(modelCheck.selectedProperty());
        initialCheck.selectedProperty().setValue(BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK));
        invariantCheck.selectedProperty().setValue(BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK));
        modelCheck.selectedProperty().setValue(BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK));
    }
}
