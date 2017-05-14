package de.bmoth.app;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Julia on 14.05.2017.
 */
public class OptionController {

    public TextField minInt;
    public TextField maxInt;
    public TextField maxInitState;
    public TextField maxTrans;


    Stage stage;

    public Stage getStage(Parent root) {
        Scene scene = new Scene(root);
        this.stage = new Stage();
        stage.setScene(scene);
        setupStage();
        return stage;
    }

    private void setupStage() {
        stage.setTitle("Options");
        minInt.setText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT)));
        maxInt.setText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT)));
        maxInitState.setText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE)));
        maxTrans.setText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS)));



    }
    private void savePrefs() {
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MIN_INT,minInt.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INT,maxInt.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE,maxInitState.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS,maxTrans.getText());

    }


    public void handleApply(ActionEvent actionEvent) {
        savePrefs();
    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }

    public void handleOk(ActionEvent actionEvent) {
        savePrefs();
        stage.close();
    }
}
