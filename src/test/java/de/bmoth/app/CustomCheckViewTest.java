package de.bmoth.app;

import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;

public class CustomCheckViewTest extends HeadlessUITest {
    @InjectViewModel
    ViewTuple<CustomCheckView, CustomCheckViewModel> viewCustomCheckViewModelViewTuple;

    @Override
    public void start(Stage stage) throws Exception {
        viewCustomCheckViewModelViewTuple = FluentViewLoader.fxmlView(CustomCheckView.class).load();
        Parent root = viewCustomCheckViewModelViewTuple.getView();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testHandleOk() {

        boolean initB = BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK);
        boolean invarB = BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK);
        boolean checkB = BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK);


        assertEquals(initB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckInit());
        assertEquals(invarB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckInvar());
        assertEquals(checkB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckModel());

        clickOn("#initialCheck");
        clickOn("#modelCheck");
        clickOn("#invariantCheck");

        verifyThat("#invariantCheck", isNotNull());
        assertEquals(!initB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckInit());
        assertEquals(!invarB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckInvar());
        assertEquals(!checkB, viewCustomCheckViewModelViewTuple.getViewModel().isCheckModel());

        Future<Void> okClick = WaitForAsyncUtils.asyncFx(() -> viewCustomCheckViewModelViewTuple.getCodeBehind().handleOk());
        WaitForAsyncUtils.waitFor(okClick);

        verifyThat("#invariantCheck", isNull());
        assertEquals(!initB, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INITIAL_CHECK));
        assertEquals(!invarB, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.INVARIANT_CHECK));
        assertEquals(!checkB, BMothPreferences.getBooleanPreference(BMothPreferences.BooleanPreference.MODEL_CHECK));
    }

    @Test
    public void handleClose() {
        verifyThat("#invariantCheck", isNotNull());

        Future<Void> okClick = WaitForAsyncUtils.asyncFx(() -> viewCustomCheckViewModelViewTuple.getCodeBehind().handleClose());
        WaitForAsyncUtils.waitFor(okClick);

        verifyThat("#invariantCheck", isNull());
    }


}
