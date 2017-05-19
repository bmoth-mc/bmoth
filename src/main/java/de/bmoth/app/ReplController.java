package de.bmoth.app;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.SolutionFinder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ReplController implements Initializable {

    @FXML
    TextArea replText = new TextArea();

    private Context ctx;
    private Solver s;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        ctx = new Context();
        s = ctx.mkSolver();

        replText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String[] predicate = replText.getText().split("\n");
                String solution = processPredicate(predicate[predicate.length-1]);
                replText.appendText(solution);
            }
        });
    }

    private String processPredicate(String predicate) {
        ctx = new Context();
        s = ctx.mkSolver();
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(predicate, ctx);
        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(1);
        if (solutions.size() == 0) {
            return "\nUNSATISFIABLE";
        } else {
            return "\n" + solutions.toString();
        }
    }
}
