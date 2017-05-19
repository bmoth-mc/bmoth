package de.bmoth.app;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class CustomCheckController {

    @FXML
    CheckBox invariantCheck;
    @FXML
    CheckBox modelCheck;

    Stage stage;

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
        invariantCheck.setSelected(true);
        modelCheck.setSelected(true);
    }

    private void savePrefs() {
        PersonalPreferences.setBooleanPreference(PersonalPreferences.BooleanPreference.INVARIANT_CHECK, invariantCheck.isSelected());
        PersonalPreferences.setBooleanPreference(PersonalPreferences.BooleanPreference.MODEL_CHECK, modelCheck.isSelected());
    }


    public void handleApply() {
            savePrefs();
    }

    public void handleClose() {
        stage.close();
    }

    public void handleOk() {
            savePrefs();
            stage.close();
    }
}
