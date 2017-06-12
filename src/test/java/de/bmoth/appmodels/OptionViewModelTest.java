package de.bmoth.appmodels;

import de.bmoth.app.OptionViewModel;
import javafx.beans.property.StringProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class OptionViewModelTest {
    private OptionViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new OptionViewModel();
    }

    private void noNumericInputTest(StringProperty input) {
        input.set("a");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void minIntNotNumeric() {
        noNumericInputTest(viewModel.getMinInt());
    }

    @Test
    public void maxIntNotNumeric() {
        noNumericInputTest(viewModel.getMaxInt());
    }

    @Test
    public void maxInitStateNotNumeric() {
        noNumericInputTest(viewModel.getMaxInitState());
    }

    @Test
    public void maxTransNotNumeric() {
        noNumericInputTest(viewModel.getMaxTrans());
    }

    @Test
    public void z3TimeOutNotNumeric() {
        noNumericInputTest(viewModel.getZ3Timeout());
    }

    @Test
    public void z3TimeOutTooSmall() {
        viewModel.getZ3Timeout().set("-1");
        assertFalse(viewModel.checkPrefs());
        assertEquals("Timout needs to be a positive Value", viewModel.getAlertText().get());
    }

    @Test
    public void maxInitStateTooSmall() {
        viewModel.getMaxInitState().set("-1");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void maxTransTooSmall() {
        viewModel.getMaxTrans().set("-1");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void minIntBiggerThenMaxInt() {
        viewModel.getMaxInt().set("1");
        viewModel.getMinInt().set("2");
        assertFalse(viewModel.checkPrefs());
    }
}
