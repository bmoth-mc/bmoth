package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class AppControllerTest extends HeadlessUITest {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void clickingOptionsOpensDialog() {
        clickOn("#fileMenu");
        clickOn("#options");

        verifyThat("#minInt", isVisible());
    }

    @Test
    @Ignore("not working on travis")
    public void clickingReplOpensRepl() {
        clickOn("#replMenu");
        clickOn("#openRepl");

        verifyThat("#replText", isVisible());
    }
}
