package de.bmoth.app;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupStage;

public abstract class HeadlessUITest extends ApplicationTest {
    @BeforeClass
    public static void setupHeadlessIfRequested() throws TimeoutException {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        registerPrimaryStage();
    }

    @After
    public void cleanup() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();
        setupStage(Stage::close);
    }
}
