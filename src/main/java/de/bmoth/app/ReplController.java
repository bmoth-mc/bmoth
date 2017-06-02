package de.bmoth.app;

import com.microsoft.z3.*;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.FormulaNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

public class ReplController implements Initializable {
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
            }
        });
    }


    private String processPredicate(String predicate) {
        ctx = new Context();
        s = ctx.mkSolver();
        FormulaNode node = Parser.getFormulaAsSemanticAst(predicate);
        boolean concatFlag = false;
        if (node.getFormulaType()!=PREDICATE_FORMULA) {
            String concatFormula = "x="+predicate;
            FormulaNode concatNode = Parser.getFormulaAsSemanticAst(concatFormula);
            if (concatNode.getFormulaType()!=PREDICATE_FORMULA) {
                throw new IllegalArgumentException("Input can not be extended to a predicate via an additional variable.");
            }
            else
            {
                predicate = concatFormula;
                concatFlag = true;
            }
        }
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(predicate, ctx);
        s.add(constraint);
        Status check = s.check();

        if (check == Status.SATISFIABLE) {
            Model model = s.getModel();
            String output = new PrettyPrinter(model).getOutput();
            if (model.toString().equals("")) {
                return "\n" + check;
            } else {
                if (concatFlag) {
                    String concatOutput = output.substring(3,output.length()-1);
                    System.out.println("concatOutput="+concatOutput);
                    return "\n" + concatOutput;
                }
                else {
                    return "\n" + output;
                }
            }
        } else {
            return "\n" + Status.UNSATISFIABLE;
        }
    }
}
