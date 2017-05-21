package de.bmoth.app;

import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

public abstract class HeadlessUITest extends ApplicationTest {
    @BeforeClass
    public static void setupHeadlessIfRequested() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        registerPrimaryStage();
    }
}
