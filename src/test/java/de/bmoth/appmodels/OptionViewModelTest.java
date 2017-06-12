package de.bmoth.appmodels;

import de.bmoth.app.OptionViewModel;
import javafx.beans.property.StringProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;


public class OptionViewModelTest {
    OptionViewModel viewModel;

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
        noNumericInputTest(viewModel.minInt);
    }

    @Test
    public void maxIntNotNumeric() {
        noNumericInputTest(viewModel.maxInt);
    }

    @Test
    public void maxInitStateNotNumeric() {
        noNumericInputTest(viewModel.maxInitState);
    }

    @Test
    public void maxTransNotNumeric() {
        noNumericInputTest(viewModel.maxTrans);
    }

    @Test
    public void z3TimeOutNotNumeric() {
        noNumericInputTest(viewModel.z3Timeout);
    }

    @Test
    public void z3TimeOutTooSmall() {
        viewModel.z3Timeout.set("-1");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void maxInitStateTooSmall() {
        viewModel.maxInitState.set("-1");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void maxTransTooSmall() {
        viewModel.maxTrans.set("-1");
        assertFalse(viewModel.checkPrefs());
    }

    @Test
    public void minIntBiggerThenMaxInt() {
        viewModel.maxInt.set("1");
        viewModel.minInt.set("2");
        assertFalse(viewModel.checkPrefs());
    }
}
