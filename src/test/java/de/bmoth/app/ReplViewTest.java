package de.bmoth.app;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static de.bmoth.TestUsingZ3.translatePredicate;
import static javafx.scene.input.KeyCode.ENTER;
import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class ReplViewTest extends HeadlessUITest {
    private TextArea repl;

    @Override
    public void start(Stage stage) throws Exception {

        ViewTuple<ReplView, ReplViewModel> viewReplViewModelViewTuple = FluentViewLoader.fxmlView(ReplView.class).load();
        Parent root = viewReplViewModelViewTuple.getView();
        Scene scene = new Scene(root);
        stage.setTitle("REPL");
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setRepl() {
        repl = lookup("#replText").query();
    }

    @Test
    public void replTypeSimplePredicateTest() {
        clickOn(repl).write("x = 5").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = 5\n{x=5}\n"));
    }

    @Test
    public void replTypeUnsatisfiablePredicateTest() {
        clickOn(repl).write("x = 5 & x = 6").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = 5 & x = 6\nUNSATISFIABLE\n"));
    }

    @Test
    public void replTypeSetPredicateTest() {
        clickOn(repl).write("x = {1}").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("x = {1}\n{x={1}}\n"));
    }

    @Test
    public void replIssue71Test() {
        clickOn(repl).write("3 > 2").push(ENTER);
        waitForRepl();
        verifyThat(repl, hasText("3 > 2\nSATISFIABLE\n"));
    }

    private void waitForRepl() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void formatCouplesInSetTest() {
        Context ctx = new Context();
        Solver s = ctx.mkSolver();
        BoolExpr constraint = translatePredicate("x = {(1,2,3),(4,5,6)}", ctx);
        s.add(constraint);
        s.check();

        Model model = s.getModel();
        String output = new PrettyPrinter(model).getOutput();
        assertEquals("{x={((1,2),3),((4,5),6)}}", output);
    }
}
