package de.bmoth.app;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class AppTest {

    private Stage stage;

    @Test
    public void testApp() throws InterruptedException {

        new JFXPanel(); // Initializes the JavaFx Platform
        Future<Void> f = WaitForAsyncUtils.asyncFx(() -> {
            try {
                stage = new Stage();
                new App().start(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        WaitForAsyncUtils.waitFor(f);
        assertEquals(true, stage.isShowing());
        Platform.runLater(() -> stage.close());
    }


}
