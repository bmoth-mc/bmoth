package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;


public class OptionControllerTest extends HeadlessUITest {
    private static final String MIN_INT_ID = "#minInt";
    private static final String MAX_INT_ID = "#maxInt";
    private static final String MAX_INIT_STATE_ID = "#maxInitState";
    private static final String MAX_TRANSITIONS_ID = "#maxTrans";
    private static final String Z3_TIMEOUT_ID = "#z3Timeout";

    private OptionController optionController;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OptionView.fxml"));
        Parent root = loader.load();
        optionController = loader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setup() {
        optionController.setUpPrefs();
    }

    @Test
    public void optionCreateTest() {
        verifyThat(MIN_INT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT))));
        verifyThat(MAX_INT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT))));
        verifyThat(MAX_INIT_STATE_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(MAX_TRANSITIONS_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(Z3_TIMEOUT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT))));
    }

    @Test
    public void checkSucces() {
        doubleClickOn(MIN_INT_ID).write("1");
        doubleClickOn(MAX_INT_ID).write("100");
        doubleClickOn(MAX_INIT_STATE_ID).write("20");
        doubleClickOn(MAX_TRANSITIONS_ID).write("5");
        doubleClickOn(Z3_TIMEOUT_ID).write("5000");
        assertEquals(true, optionController.checkPrefs());
    }

    @Test
    public void checkMinBiggerMax() {
        doubleClickOn(MIN_INT_ID).eraseText(3).write("3");
        doubleClickOn(MAX_INT_ID).write("1");
        doubleClickOn(MAX_INIT_STATE_ID).write("20");
        doubleClickOn(MAX_TRANSITIONS_ID).write("5");
        doubleClickOn(Z3_TIMEOUT_ID).write("5000");
        clickOn("#applyButton");
    }

    @Test
    public void closeOnSuccess() {
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MIN_INT, "-1");
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INT, "3");
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE, "5");
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS, "5");
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT, "5000");
        optionController.setUpPrefs();
        verifyThat(MIN_INT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT))));
        verifyThat(MAX_INT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT))));
        verifyThat(MAX_INIT_STATE_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(MAX_TRANSITIONS_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(Z3_TIMEOUT_ID, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT))));
        //Note: DoubleClick doesn't select the -, so it has to be removed.
        doubleClickOn(MIN_INT_ID).eraseText(3).write("-3");
        doubleClickOn(MAX_INT_ID).write("44");
        doubleClickOn(MAX_INIT_STATE_ID).write("11");
        doubleClickOn(MAX_TRANSITIONS_ID).write("13");
        doubleClickOn(Z3_TIMEOUT_ID).write("5003");
        clickOn("#applyButton");
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT), -3);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT), 44);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE), 11);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS), 13);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT), 5003);
        verifyThat(MIN_INT_ID, isNotNull());
        clickOn("#okButton");
        verifyThat(MIN_INT_ID, isNull());
    }

    private void noNumericInputTest(String input) {
        doubleClickOn(input).eraseText(10).write("a");
        Platform.runLater(() -> assertEquals(false, optionController.checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void minIntNotNumeric() {
        noNumericInputTest(MIN_INT_ID);
    }

    @Test
    public void maxIntNotNumeric() {
        noNumericInputTest(MAX_INT_ID);
    }

    @Test
    public void maxInitStateNotNumeric() {
        noNumericInputTest(MAX_INIT_STATE_ID);
    }

    @Test
    public void maxTransNotNumeric() {
        noNumericInputTest(MAX_TRANSITIONS_ID);
    }

    @Test
    public void z3TimeOutNotNumeric() {
        noNumericInputTest(Z3_TIMEOUT_ID);
    }

    @Test
    public void z3TimeOutTooSmall() {
        doubleClickOn(Z3_TIMEOUT_ID).eraseText(3).write("-1");
        Platform.runLater(() -> assertEquals(false, optionController.checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void maxInitStateTooSmall() {
        doubleClickOn(MAX_INIT_STATE_ID).eraseText(3).write("-1");
        Platform.runLater(() -> assertEquals(false, optionController.checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void maxTransTooSmall() {
        doubleClickOn(MAX_TRANSITIONS_ID).eraseText(3).write("-1");
        Platform.runLater(() -> assertEquals(false, optionController.checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void minIntBiggerThenMaxInt() {
        doubleClickOn(MAX_INT_ID).eraseText(3).write("1");
        doubleClickOn(MIN_INT_ID).eraseText(3).write("2");
        Platform.runLater(() -> assertEquals(false, optionController.checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

}
