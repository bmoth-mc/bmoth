package de.bmoth.app;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_sort_kind;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReplController implements Initializable {
    private final Logger logger = Logger.getLogger(getClass().getName());


    @FXML
    TextArea replText;

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
                replText.commitValue();
            }
        });
    }

    private String processPredicate(String predicate) {
        ctx = new Context();
        s = ctx.mkSolver();
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(predicate, ctx);

        s.add(constraint);
        Status check = s.check();

        if (check == Status.SATISFIABLE) {
            Model model = s.getModel();
            StringJoiner output = new StringJoiner(", ", "{", "}");
            FuncDecl[] functionDeclarations = model.getConstDecls();
            for (FuncDecl decl : functionDeclarations) {
                try {
                    if (decl.getArity() == 0 && decl.getRange().getSortKind() != Z3_sort_kind.Z3_ARRAY_SORT) {
                        // this is a constant
                        output.add(decl.getName().toString() + "=" + model.getConstInterp(decl));
                    } else {
                        // not a constant, e.g. some representation of a set
                        StringJoiner setRepresentation = new StringJoiner(",", "{", "}");
                        FuncInterp functionInterpretation = model.getFuncInterp(decl);
                        for (FuncInterp.Entry entry : functionInterpretation.getEntries()) {
                            for (Expr entryArg : entry.getArgs()) {
                                setRepresentation.add(entryArg.toString());
                            }
                        }
                        output.add(decl.getName().toString() + "=" + setRepresentation.toString());
                    }

                } catch (com.microsoft.z3.Z3Exception e) {
                    logger.log(Level.SEVERE, "Z3 exception while solving", e);
                }
            }
            if (model.toString().equals("")) {
                return "\n" + check;
            } else {
                return "\n" + output.toString();
            }
        } else {
            return "\n" + Status.UNSATISFIABLE;
        }
    }
}
