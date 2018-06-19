package de.bmoth.app;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

public class ReplView implements FxmlView<ReplViewModel>, Initializable {
    @InjectViewModel
    ReplViewModel replViewModel;
    @FXML
    TextArea replText;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        replViewModel.getCode().bindBidirectional(replText.textProperty());

        replText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                replViewModel.processPredicate();
                replText.positionCaret(replText.getLength());
                keyEvent.consume();
            }
        });
    }
}
