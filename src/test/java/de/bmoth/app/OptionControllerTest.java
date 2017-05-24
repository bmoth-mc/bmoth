package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;


public class OptionControllerTest extends HeadlessUITest {
    private static final String minIntId = "#minInt";
    private static final String maxIntId = "#maxInt";
    private static final String maxInitStateId = "#maxInitState";
    private static final String maxTransitionsId = "#maxTrans";
    private static final String z3TimeoutId = "#z3Timeout";

    private OptionController optionController;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
        Parent root = loader.load();
        optionController = loader.getController();
        optionController.getStage(root).show();
    }

    @Before
    public void setup() {
        optionController.setUpPrefs();
    }

    @Test
    public void optionCreateTest() {
        verifyThat(minIntId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT))));
        verifyThat(maxIntId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT))));
        verifyThat(maxInitStateId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(maxTransitionsId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(z3TimeoutId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT))));
    }

    @Test
    public void checkSucces() {
        doubleClickOn(minIntId).write("1");
        doubleClickOn(maxIntId).write("100");
        doubleClickOn(maxInitStateId).write("20");
        doubleClickOn(maxTransitionsId).write("5");
        doubleClickOn(z3TimeoutId).write("5000");
        assertEquals(true, optionController.checkPrefs());
    }

    @Test
    public void checkMinBiggerMax() {
        doubleClickOn(minIntId).eraseText(2).write("3");
        doubleClickOn(maxIntId).write("1");
        doubleClickOn(maxInitStateId).write("20");
        doubleClickOn(maxTransitionsId).write("5");
        doubleClickOn(z3TimeoutId).write("5000");
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
        verifyThat(minIntId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT))));
        verifyThat(maxIntId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT))));
        verifyThat(maxInitStateId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(maxTransitionsId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(z3TimeoutId, hasText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT))));
        //Note: DoubleClick doesn't select the -, so it has to be removed.
        doubleClickOn(minIntId).eraseText(2).write("-3");
        doubleClickOn(maxIntId).write("44");
        doubleClickOn(maxInitStateId).write("11");
        doubleClickOn(maxTransitionsId).write("13");
        doubleClickOn(z3TimeoutId).write("5003");
        clickOn("#applyButton");
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT), -3);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT), 44);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE), 11);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS), 13);
        assertEquals(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT), 5003);
    }


}
