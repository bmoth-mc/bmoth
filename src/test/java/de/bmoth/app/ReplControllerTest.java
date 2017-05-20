package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static javafx.scene.input.KeyCode.ENTER;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

/**
 * Created by krings on 20.05.17.
 */
public class ReplControllerTest extends ApplicationTest {
    private ReplController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("repl.fxml"));
        controller = loader.getController();
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void replTypeSimplePredicateTest() {
        clickOn("#replText").write("x = 5");
        verifyThat("#replText", hasText("x = 5"));

        push(ENTER);
        verifyThat("#replText", hasText("x = 5\n{x=5}\n"));
    }
}
