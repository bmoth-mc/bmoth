package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static javafx.scene.input.KeyCode.ENTER;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class ReplControllerTest extends HeadlessUITest {
    private TextArea repl;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("repl.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setRepl() {
        repl = lookup("#replText").query();
    }

    @Test
    public void replTypeSimplePredicateTest() {
        clickOn(repl).write("x = 5").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = 5\n{x=5}\n"));
    }

    @Test
    public void replTypeUnsatisfiablePredicateTest() {
        clickOn(repl).write("x = 5 & x = 6").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = 5 & x = 6\nUNSATISFIABLE\n"));
    }

    @Test
    public void replTypeSetPredicateTest() {
        clickOn(repl).write("x = {1}").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = {1}\n{x={1}}\n"));
    }

    @Test
    public void replIssue71Test() {
        clickOn(repl).write("3 > 2").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("3 > 2\nSATISFIABLE\n"));
    }

    private void waitForRepl() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
        WaitForAsyncUtils.waitForFxEvents();
    }
}
