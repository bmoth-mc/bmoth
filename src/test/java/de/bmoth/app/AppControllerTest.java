package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

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
    public void opensUnsavedChangesWarning() {
        clickOn("#codeArea").write(" ");
        clickOn("#fileMenu").clickOn("Exit");
        // the warning dialog opens, codeArea stays open
        verifyThat("Back", isNotNull());
        verifyThat("#codeArea", isNotNull());

        // click the back button
        clickOn("Back");
        // dialog closes, were back to the code area
        verifyThat("Back", isNull());
        verifyThat("#codeArea", isNotNull());
    }

    @Test
    public void clickingReplOpensRepl() throws InterruptedException {
        verifyThat("#replText", isNull());

        clickOn("#replMenu").clickOn("#openRepl");

        verifyThat("#replText", isNotNull());

        // close the repl window so that later tests are not confused
        Node repl = lookup("REPL").query();
        Stage scene = (Stage) repl.getScene().getWindow();
        interact(() -> scene.close());
        WaitForAsyncUtils.waitForFxEvents();
    }
}
