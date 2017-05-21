package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;

import static javafx.scene.input.KeyCode.ENTER;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

public class ReplControllerTest extends HeadlessUITest {
    private String replId = "#replText";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("repl.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void replTypeSimplePredicateTest() {
        clickOn(replId).write("x = 5");
        verifyThat(replId, hasText("x = 5"));

        push(ENTER);
        verifyThat(replId, hasText("x = 5\n{x=5}\n"));
    }

    @Test
    public void replTypeUnsatisfiablePredicateTest() {
        clickOn(replId).write("x = 5 & x = 6");
        verifyThat(replId, hasText("x = 5 & x = 6"));

        push(ENTER);
        verifyThat(replId, hasText("x = 5 & x = 6\nUNSATISFIABLE\n"));
    }
}
