package de.bmoth.app;

import com.microsoft.z3.*;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

public class ReplViewModel implements ViewModel {

    private StringProperty code = new SimpleStringProperty();

    private Context ctx = new Context();
    private Solver s = ctx.mkSolver();

    StringProperty getCode() {
        return code;
    }

    void processPredicate() {
        String predicate = code.get().substring(code.get().lastIndexOf('\n') + 1);
        FormulaNode node;
        try {
            node = Parser.getFormulaAsSemanticAst(predicate);

            boolean concatFlag = false;
            if (node.getFormulaType() != PREDICATE_FORMULA) {
                predicate = "x=" + predicate;
                FormulaNode concatNode;
                concatNode = Parser.getFormulaAsSemanticAst(predicate);
                if (concatNode.getFormulaType() != PREDICATE_FORMULA) {
                    throw new IllegalArgumentException("Input can not be extended to a predicate via an additional variable.");
                } else {
                    concatFlag = true;
                }
            }
            BoolExpr constraint;

            constraint = FormulaToZ3Translator.translatePredicate(predicate, ctx);
            s.add(constraint);
            Status check = s.check();

            if (check == Status.SATISFIABLE) {
                Model model = s.getModel();
                String output = new PrettyPrinter(model).getOutput();
                if (model.toString().equals("")) {
                    code.set(code.get() + "\n" + check + "\n");
                } else {
                    if (concatFlag) {
                        String concatOutput = output.substring(3, output.length() - 1);
                        code.set(code.get() + "\n" + concatOutput + "\n");
                    } else {
                        code.set(code.get() + "\n" + output + "\n");
                    }
                }
            } else {
                code.set(code.get() + "\nUNSATISFIABLE\n");
            }
        } catch (ParserException e) {
            // TODO handle parser errors
            throw new RuntimeException(e);//TODO replace this
        }

    }
}
