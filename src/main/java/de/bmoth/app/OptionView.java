package de.bmoth.app;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Julian on 09.06.2017.
 */
public class OptionView implements FxmlView<OptionViewModel>, Initializable {
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

    private Alert preferenceVerificationErrorAlert;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        optionViewModel.loadPrefs();
        minInt.textProperty().bindBidirectional(optionViewModel.getMinInt());
        maxInt.textProperty().bindBidirectional(optionViewModel.getMaxInt());
        maxInitState.textProperty().bindBidirectional(optionViewModel.getMaxInitState());
        maxTrans.textProperty().bindBidirectional(optionViewModel.getMaxTrans());
        z3Timeout.textProperty().bindBidirectional(optionViewModel.getZ3Timeout());

        preferenceVerificationErrorAlert = new Alert(Alert.AlertType.ERROR);
        optionViewModel.getAlertText().bind(preferenceVerificationErrorAlert.contentTextProperty());
    }

    boolean checkPrefs() {
        if (!optionViewModel.checkPrefs()) {
            preferenceVerificationErrorAlert.show();
            return false;
        }
        return true;
    }

    public void handleApply() {
        if (checkPrefs())
            optionViewModel.savePrefs();
    }

    public void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void handleOk() {
        if (checkPrefs()) {
            optionViewModel.savePrefs();
            handleClose();
        }
    }

}
