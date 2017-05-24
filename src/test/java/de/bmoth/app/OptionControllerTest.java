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
    private OptionController optionController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
        Parent root = loader.load();
        optionController = loader.getController();
        stage = optionController.getStage(root);
        stage.show();

    }

    @Before
    public void setup() {
        optionController.setUpPrefs();
    }

    @Test
    public void optionCreateTest() {
        verifyThat("#minInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT))));
        verifyThat("#maxInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT))));
        verifyThat("#maxInitState", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat("#maxTrans", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat("#z3Timeout", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT))));
    }

    @Test
    public void checkSucces() {
        doubleClickOn("#minInt").write("1");
        doubleClickOn("#maxInt").write("100");
        doubleClickOn("#maxInitState").write("20");
        doubleClickOn("#maxTrans").write("5");
        doubleClickOn("#z3Timeout").write("5000");
        assertEquals(true, optionController.checkPrefs());
    }

    @Test
    public void checkMinBiggerMax() {
        doubleClickOn("#minInt").eraseText(2).write("3");
        doubleClickOn("#maxInt").write("1");
        doubleClickOn("#maxInitState").write("20");
        doubleClickOn("#maxTrans").write("5");
        doubleClickOn("#z3Timeout").write("5000");
        clickOn("#applyButton");
    }

    @Test
    public void closeOnSuccess() {
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MIN_INT, "-1");
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INT, "3");
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE, "5");
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS, "5");
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT, "5000");
        optionController.setUpPrefs();
        verifyThat("#minInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT))));
        verifyThat("#maxInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT))));
        verifyThat("#maxInitState", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat("#maxTrans", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat("#z3Timeout", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT))));
        //Note: DoubleClick doesn't select the -, so it has to be removed.
        doubleClickOn("#minInt").eraseText(2).write("-3");
        doubleClickOn("#maxInt").write("44");
        doubleClickOn("#maxInitState").write("11");
        doubleClickOn("#maxTrans").write("13");
        doubleClickOn("#z3Timeout").write("5003");
        clickOn("#applyButton");
        assertEquals(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT), -3);
        assertEquals(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT), 44);
        assertEquals(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE), 11);
        assertEquals(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS), 13);
        assertEquals(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT), 5003);
    }


}
