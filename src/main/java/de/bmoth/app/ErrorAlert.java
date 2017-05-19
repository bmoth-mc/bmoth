package de.bmoth.app;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class ErrorAlert extends Alert {

    public ErrorAlert(AlertType alertType, String errorType, String msg) {
        super(alertType);
        this.setTitle(errorType + "!");
        this.setHeaderText(errorType + " occurred.");
        this.setContentText(msg);
        this.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        this.showAndWait();
    }
}
