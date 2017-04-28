package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.bmoth.backend.FormulaTranslator;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

/**
 * Created by krings on 28.04.17.
 */
public class ModelChecker {
    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        FormulaTranslator translator = new FormulaTranslator(ctx);

        Set<State> visited = new HashSet<>();
        Stack<State> queue = new Stack<>();

        SingleAssignSubstitution initialization = (SingleAssignSubstitution) machine.getInitialisation();
        ExprNode initialValue = initialization.getValue();

        Expr initialValueAsZ3Expression = translator.translateExpression(initialValue, ctx);
        Sort z3TypeOfInitialValue = translator.bTypeToZ3Sort(initialValue.getType());

        Expr theIdentifier = ctx.mkConst(initialization.getIdentifier().getName(),z3TypeOfInitialValue);

        BoolExpr initialValueConstraint = ctx.mkEq(theIdentifier,initialValueAsZ3Expression);


        while(!queue.isEmpty()) {
            State current = queue.pop();

            // check invariant

            // compute successors
            // add to queue if not in visited

        }
    }
}
