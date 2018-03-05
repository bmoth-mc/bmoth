package de.bmoth.app;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class AppViewTest extends HeadlessUITest {
    private String codeAreaId = "#codeArea";

    @Override
    public void start(Stage stage) throws Exception {
        ViewTuple<AppView, AppViewModel> viewTuple = FluentViewLoader.fxmlView(AppView.class).load();
        Parent root = viewTuple.getView();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(App.class.getResource("keywords.css").toExternalForm());
        stage.setScene(scene);
        viewTuple.getCodeBehind().setupStage(stage);
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
        clickOn(codeAreaId).write(" ");
        verifyThat("#infoArea", hasText("Unsaved changes"));
    }

    @Test
    public void opensUnsavedChangesWarning() {
        clickOn(codeAreaId).write(" ");
        clickOn("#fileMenu").clickOn("Exit");
        // the warning dialog opens, codeArea stays open
        verifyThat("Back", isNotNull());
        verifyThat(codeAreaId, isNotNull());

        // click the back button
        clickOn("Back");
        // dialog closes, were back to the code area
        verifyThat("Back", isNull());
        verifyThat(codeAreaId, isNotNull());
    }

    @Test
    @Ignore //affects repl
    public void clickingReplOpensRepl() throws InterruptedException {
        verifyThat("#replText", isNull());

        clickOn("#replMenu").clickOn("#openRepl");

        verifyThat("#replText", isNotNull());

        // close the repl window so that later tests are not confused
        Node repl = lookup("REPL").query();
        Stage scene = (Stage) repl.getScene().getWindow();
        interact(scene::close);
        WaitForAsyncUtils.waitForFxEvents();
    }
}
