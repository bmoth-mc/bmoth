package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class CustomCheckControllerTest extends HeadlessUITest {
    Stage stage;
    private CustomCheckController customCheckController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customCheck.fxml"));
        Parent root = loader.load();
        customCheckController = loader.getController();
        this.stage = customCheckController.getStage(root);
        this.stage.show();
    }

    @Test
    public void testHandleOk() {
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK, true);
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK, true);
        BMothPreferences.setBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK, true);

        assertEquals(true, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK));
        assertEquals(true, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK));
        assertEquals(true, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK));

        assertEquals(true, stage.isShowing());
        assertEquals(true, customCheckController.initialCheck.isSelected());
        assertEquals(true, customCheckController.invariantCheck.isSelected());
        assertEquals(true, customCheckController.modelCheck.isSelected());

        clickOn(customCheckController.initialCheck);
        clickOn(customCheckController.modelCheck);
        clickOn(customCheckController.invariantCheck);

        assertEquals(false, customCheckController.initialCheck.isSelected());
        assertEquals(false, customCheckController.invariantCheck.isSelected());
        assertEquals(false, customCheckController.modelCheck.isSelected());

        Future<Void> okClick = WaitForAsyncUtils.asyncFx(() -> customCheckController.handleOk());
        WaitForAsyncUtils.waitFor(okClick);

        assertEquals(false, stage.isShowing());
        assertEquals(false, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK));
        assertEquals(false, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK));
        assertEquals(false, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK));
    }

    @Test
    public void handleClose() {
        assertEquals(true, stage.isShowing());

        Future<Void> okClick = WaitForAsyncUtils.asyncFx(() -> customCheckController.handleClose());
        WaitForAsyncUtils.waitFor(okClick);

        assertEquals(false, stage.isShowing());
    }


}
