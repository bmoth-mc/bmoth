package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
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
    ViewTuple<OptionView, OptionViewModel> viewOptionViewModelViewTuple;

    @Override
    public void start(Stage stage) throws Exception {
        viewOptionViewModelViewTuple = FluentViewLoader.fxmlView(OptionView.class).load();
        Parent root = viewOptionViewModelViewTuple.getView();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setup() {
        viewOptionViewModelViewTuple.getViewModel().loadPrefs();
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
    public void checkSuccess() {
        doubleClickOn(MIN_INT_ID).write("1");
        doubleClickOn(MAX_INT_ID).write("100");
        doubleClickOn(MAX_INIT_STATE_ID).write("20");
        doubleClickOn(MAX_TRANSITIONS_ID).write("5");
        doubleClickOn(Z3_TIMEOUT_ID).write("5000");
        assertEquals(true, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs());
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
        viewOptionViewModelViewTuple.getViewModel().loadPrefs();
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
        Platform.runLater(() -> assertEquals(false, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs()));
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
        Platform.runLater(() -> assertEquals(false, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void maxInitStateTooSmall() {
        doubleClickOn(MAX_INIT_STATE_ID).eraseText(3).write("-1");
        Platform.runLater(() -> assertEquals(false, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void maxTransTooSmall() {
        doubleClickOn(MAX_TRANSITIONS_ID).eraseText(3).write("-1");
        Platform.runLater(() -> assertEquals(false, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void minIntBiggerThenMaxInt() {
        doubleClickOn(MAX_INT_ID).eraseText(3).write("1");
        doubleClickOn(MIN_INT_ID).eraseText(3).write("2");
        Platform.runLater(() -> assertEquals(false, viewOptionViewModelViewTuple.getCodeBehind().checkPrefs()));
        WaitForAsyncUtils.waitForFxEvents();
    }

}
