package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;


public class OptionControllerTest extends ApplicationTest {
    private OptionController optionController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
        Parent root = loader.load();
        optionController = loader.getController();
        Stage optionStage = optionController.getStage(root);
        optionStage.show();
    }

    @Test
    public void optionCreateTest() {
        optionController.setUpPrefs();
        verifyThat("#minInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT))));
        verifyThat("#maxInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT))));
        verifyThat("#maxInitState", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat("#maxTrans", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS))));
        verifyThat("#z3Timeout", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT))));
    }
}
