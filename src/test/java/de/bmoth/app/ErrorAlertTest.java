package de.bmoth.app;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class ErrorAlertTest {

    private static final String ERROR_TYPE_TEST = "error.type.test";
    private static final String ERROR_MSG_TEST = "error.msg.test";

    private ErrorAlert alert;

    @Test
    public void testErrorAlert() {
        new JFXPanel(); // Initializes the JavaFx Platform
        Future<Void> f = WaitForAsyncUtils.asyncFx(() -> {
            alert = new ErrorAlert(Alert.AlertType.ERROR, ERROR_TYPE_TEST, ERROR_MSG_TEST);});
        WaitForAsyncUtils.waitFor(f);
        assertEquals(Alert.AlertType.ERROR, alert.getAlertType());
        assertEquals(ERROR_TYPE_TEST + "!", alert.getTitle());
        assertEquals(ERROR_TYPE_TEST + " occurred.", alert.getHeaderText());
        assertEquals(ERROR_MSG_TEST, alert.getContentText());
    }
}
