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
    private static final String minIntID = "#minInt";
    private static final String maxIntID = "#maxInt";
    private static final String maxInitStateID = "#maxInitState";
    private static final String maxTransitionsID = "#maxTrans";
    private static final String z3TimeoutID = "#z3Timeout";

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
        verifyThat(minIntID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MIN_INT))));
        verifyThat(maxIntID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_INT))));
        verifyThat(maxInitStateID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(maxTransitionsID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(z3TimeoutID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.Z3_TIMEOUT))));
    }

    @Test
    public void checkSucces() {
        doubleClickOn(minIntID).write("1");
        doubleClickOn(maxIntID).write("100");
        doubleClickOn(maxInitStateID).write("20");
        doubleClickOn(maxTransitionsID).write("5");
        doubleClickOn(z3TimeoutID).write("5000");
        assertEquals(true, optionController.checkPrefs());
    }

    @Test
    public void checkMinBiggerMax() {
        doubleClickOn(minIntID).eraseText(2).write("3");
        doubleClickOn(maxIntID).write("1");
        doubleClickOn(maxInitStateID).write("20");
        doubleClickOn(maxTransitionsID).write("5");
        doubleClickOn(z3TimeoutID).write("5000");
        clickOn("#applyButton");
    }

    @Test
    public void closeOnSuccess() {
        Preferences.setIntPreference(Preferences.IntPreference.MIN_INT, "-1");
        Preferences.setIntPreference(Preferences.IntPreference.MAX_INT, "3");
        Preferences.setIntPreference(Preferences.IntPreference.MAX_INITIAL_STATE, "5");
        Preferences.setIntPreference(Preferences.IntPreference.MAX_TRANSITIONS, "5");
        Preferences.setIntPreference(Preferences.IntPreference.Z3_TIMEOUT, "5000");
        optionController.setUpPrefs();
        verifyThat(minIntID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MIN_INT))));
        verifyThat(maxIntID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_INT))));
        verifyThat(maxInitStateID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat(maxTransitionsID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat(z3TimeoutID, hasText(String.valueOf(Preferences.getIntPreference(Preferences.IntPreference.Z3_TIMEOUT))));
        //Note: DoubleClick doesn't select the -, so it has to be removed.
        doubleClickOn(minIntID).eraseText(2).write("-3");
        doubleClickOn(maxIntID).write("44");
        doubleClickOn(maxInitStateID).write("11");
        doubleClickOn(maxTransitionsID).write("13");
        doubleClickOn(z3TimeoutID).write("5003");
        clickOn("#applyButton");
        assertEquals(Preferences.getIntPreference(Preferences.IntPreference.MIN_INT), -3);
        assertEquals(Preferences.getIntPreference(Preferences.IntPreference.MAX_INT), 44);
        assertEquals(Preferences.getIntPreference(Preferences.IntPreference.MAX_INITIAL_STATE), 11);
        assertEquals(Preferences.getIntPreference(Preferences.IntPreference.MAX_TRANSITIONS), 13);
        assertEquals(Preferences.getIntPreference(Preferences.IntPreference.Z3_TIMEOUT), 5003);
    }


}
