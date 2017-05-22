package de.bmoth.app;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_sort_kind;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
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
                String solution = processPredicate(predicate[predicate.length - 1]);
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
        StringBuilder output = new StringBuilder();
        for (Model solution : solutions) {
            FuncDecl[] functionDeclarations = solution.getConstDecls();
            for (FuncDecl decl : functionDeclarations) {
                output.append(decl.getName()).append("=");
                try {
                    if (decl.getArity() == 0 && decl.getRange().getSortKind() != Z3_sort_kind.Z3_ARRAY_SORT) {
                        // this is a constant
                        output.append(solution.getConstInterp(decl));
                    } else {
                        // not a constant, e.g. some representation of a set
                        output.append(solution.getFuncInterp(decl));
                    }
                    output.append(", ");
                } catch (com.microsoft.z3.Z3Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (solutions.isEmpty()) {
            return "\nUNSATISFIABLE";
        } else {
            return "\n{" + output.substring(0, output.length() - 2) + "}";
        }
    }
}
