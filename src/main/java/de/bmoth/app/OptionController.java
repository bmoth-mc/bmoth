package de.bmoth.app;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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
    public CheckBox checkInit;
    public CheckBox checkInVar;


    Stage stage;

    public Stage getStage(Parent root) {
        if(stage!=null) return stage;
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
        // selects end of first Textfield for Caret
        minInt.requestFocus();
        minInt.selectRange(99,99);

        checkInit.setSelected(PersonalPreferences.getBooleanPreference(PersonalPreferences.BooleanPreference.CHECK_INITIAL));
        checkInVar.setSelected(PersonalPreferences.getBooleanPreference(PersonalPreferences.BooleanPreference.CHECK_INVARIANT));
    }

    private boolean checkPrefs(){
        if(!isNumeric(minInt.getText())) {
            new Alert(Alert.AlertType.ERROR, "Needs to be Numeric:" + minInt.getId()).show();
        return false;
        }
        if(!isNumeric(maxInt.getText())) {
            new Alert(Alert.AlertType.ERROR, "Needs to be Numeric:" + maxInt.getId()).show();
        return false;
        }
            if(!isNumeric(maxInitState.getText())) {
            new Alert(Alert.AlertType.ERROR,"Needs to be Numeric:" + maxInitState.getId()).show();
        return false;
    }

        if(!isNumeric(maxTrans.getText())) {
            new Alert(Alert.AlertType.ERROR,"Needs to be Numeric:" + maxTrans.getId()).show();
        return false;
    }

        if(Integer.parseInt(minInt.getText()) > Integer.parseInt(maxInt.getText())) {
            new Alert(Alert.AlertType.ERROR, "MIN_INT bigger than MAX_INT").show();
        return false;
        }

        return true;
    }


    private void savePrefs() {
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MIN_INT,minInt.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INT,maxInt.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE,maxInitState.getText());
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS,maxTrans.getText());
        PersonalPreferences.setBooleanPreference(PersonalPreferences.BooleanPreference.CHECK_INITIAL,checkInit.isSelected());
        PersonalPreferences.setBooleanPreference(PersonalPreferences.BooleanPreference.CHECK_INVARIANT,checkInVar.isSelected());
    }


    public void handleApply(ActionEvent actionEvent) {
        if(checkPrefs())
        savePrefs();
    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }

    public void handleOk(ActionEvent actionEvent) {
        if(checkPrefs()){
        savePrefs();
        stage.close();
        }
    }

    public boolean isNumeric(String s){
        try{
            int n = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
