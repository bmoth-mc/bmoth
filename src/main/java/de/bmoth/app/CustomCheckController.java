package de.bmoth.app;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class CustomCheckController {

    @FXML
    CheckBox initialCheck;
    @FXML
    CheckBox invariantCheck;
    @FXML
    CheckBox modelCheck;

    Stage stage;
    AppController appController;

    public Stage getStage(Parent root) {
        if (stage != null) return stage;
        Scene scene = new Scene(root);
        this.stage = new Stage();
        stage.setScene(scene);
        setupStage();
        return stage;
    }

    private void setupStage() {
        stage.setTitle("CustomCheck");
        initialCheck.setSelected(true);
        invariantCheck.setSelected(true);
        modelCheck.setSelected(true);
    }

    private void savePrefs() {
        Preferences.setBooleanPreference(Preferences.BooleanPreference.INITIAL_CHECK, initialCheck.isSelected());
        Preferences.setBooleanPreference(Preferences.BooleanPreference.INVARIANT_CHECK, invariantCheck.isSelected());
        Preferences.setBooleanPreference(Preferences.BooleanPreference.MODEL_CHECK, modelCheck.isSelected());
    }


    public void handleClose() {
        stage.close();
    }

    public void handleOk() {
        savePrefs();
        stage.close();
        if (this.initialCheck.isSelected()) {
            appController.handleInitialStateExists();
        }
        if (this.invariantCheck.isSelected()) {
            appController.handleInvariantSatisfiability();
        }
        if (this.modelCheck.isSelected()) {
            appController.handleCheck();
        }
    }

    public void setAppControllerReference(AppController appCtrl) {
        this.appController = appCtrl;
    }
}
