package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class AppControllerTest extends HeadlessUITest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        AppController appController = loader.getController();
        appController.setupStage(stage);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void clickingOptionsOpensDialog() {
        clickOn("#fileMenu");
        clickOn("#options");

        verifyThat("#minInt", isNotNull());
        clickOn("Close");
        verifyThat("#minInt", isNull());
    }

    @Test
    public void checkChangesDetected() {
        clickOn("#codeArea").write(" ");
        verifyThat("#infoArea", hasText("Unsaved changes"));
    }

    @Test
    @Ignore("this test confuses the other repl tests, because it does not close the repl window")
    public void clickingReplOpensRepl() {
        verifyThat("#replText", isNull());

        clickOn("#replMenu").clickOn("#openRepl");

        verifyThat("#replText", isNotNull());
    }
}
