package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static javafx.scene.input.KeyCode.ENTER;
import static org.junit.Assert.assertEquals;

public class ReplControllerTest extends HeadlessUITest {
    private int z3WaitTime = 1000;
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
        clickOn(repl).write("x = 5").push(ENTER).sleep(z3WaitTime);
        assertEquals("x = 5\n{x=5}\n", repl.getText());
    }

    @Test
    public void replTypeUnsatisfiablePredicateTest() {
        clickOn(repl).write("x = 5 & x = 6").push(ENTER).sleep(z3WaitTime);
        assertEquals("x = 5 & x = 6\nUNSATISFIABLE\n", repl.getText());
    }

    @Test
    public void replTypeSetPredicateTest() {
        clickOn(repl).write("x = {1}").push(ENTER).sleep(z3WaitTime);
        assertEquals("x = {1}\n{x={1}}\n", repl.getText());
    }

    @Test
    public void replIssue71Test() {
        clickOn(repl).write("3 > 2").push(ENTER).sleep(z3WaitTime);
        assertEquals("3 > 2\nSATISFIABLE\n", repl.getText());
    }

    @After
    public void clickingReplOpensRepl() throws InterruptedException {
        // close the repl window so that later tests are not confused
        Stage scene = (Stage) repl.getScene().getWindow();
        interact(() -> scene.close());
        WaitForAsyncUtils.waitForFxEvents();
    }
}
