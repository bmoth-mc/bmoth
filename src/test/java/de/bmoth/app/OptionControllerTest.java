package de.bmoth.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.Test;
import org.loadui.testfx.Assertions;
import org.loadui.testfx.GuiTest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

public class OptionControllerTest extends GuiTest {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private OptionController optionController;

    @Override
    protected Parent getRootNode() {
        Parent parent;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("options.fxml"));
            parent = fxmlLoader.load();
            optionController = fxmlLoader.getController();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't create parent", e);
            return null;
        }
        return parent;
    }


    @Test
    public void optionCreateTest() {
        optionController.setUpPrefs();
        verifyThat("#minInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT))));
        verifyThat("#maxInt", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT))));
        verifyThat("#maxInitState", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE))));
        verifyThat("#maxTrans", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS))));
        Assertions.verifyThat("#z3Timeout", hasText(String.valueOf(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT))));
    }
}
