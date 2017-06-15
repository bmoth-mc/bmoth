package de.bmoth.app;

import com.microsoft.z3.*;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

public class ReplView implements FxmlView<ReplViewModel>, Initializable {

    @InjectViewModel
    ReplViewModel replViewModel;
    @FXML
    TextArea replText;

    private Context ctx;
    private Solver s;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        replViewModel.code.bind(replText.textProperty());

        ctx = new Context();
        s = ctx.mkSolver();

        replText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String[] predicate = replViewModel.code.get().split("\n");
                String solution = processPredicate(predicate[predicate.length - 1]);
                replText.appendText(solution);
            }
        });
    }


    private String processPredicate(String predicate) {
        String formula = predicate;
        ctx = new Context();
        s = ctx.mkSolver();
        FormulaNode node;
        try {
            node = Parser.getFormulaAsSemanticAst(predicate);
        } catch (ParserException e) {
            // TODO handle parser errors
            throw new RuntimeException(e);//TODO replace this
        }
        boolean concatFlag = false;
        if (node.getFormulaType() != PREDICATE_FORMULA) {
            formula = "x=" + formula;
            FormulaNode concatNode;
            try {
                concatNode = Parser.getFormulaAsSemanticAst(formula);
                if (concatNode.getFormulaType() != PREDICATE_FORMULA) {
                    throw new IllegalArgumentException("Input can not be extended to a predicate via an additional variable.");
                } else {
                    concatFlag = true;
                }
            } catch (ParserException e) {
                // TODO handle parser errors
                throw new RuntimeException(e);//TODO replace this
            }

        }
        BoolExpr constraint;
        try {
            constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
            s.add(constraint);
            Status check = s.check();

            if (check == Status.SATISFIABLE) {
                Model model = s.getModel();
                String output = new PrettyPrinter(model).getOutput();
                if (model.toString().equals("")) {
                    return "\n" + check;
                } else {
                    if (concatFlag) {
                        String concatOutput = output.substring(3, output.length() - 1);
                        return "\n" + concatOutput;
                    } else {
                        return "\n" + output;
                    }
                }
            } else {
                return "\n" + Status.UNSATISFIABLE;
            }
        } catch (ParserException e) {
         // TODO handle parser errors
            throw new RuntimeException(e);//TODO replace this
        }

    }
}
