package de.bmoth.modelchecker;

import com.microsoft.z3.*;
import de.bmoth.backend.FormulaTranslator;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

/**
 * Created by krings on 28.04.17.
 */
public class ModelChecker {
    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        FormulaTranslator translator = new FormulaTranslator(ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();

        // prepare initial state value

        // TODO implement FormulaTranslator::visitSingleAssignSubstitution(...)
        // BoolExpr initialValueConstraint = translator.visitSingleAssignSubstitution(initialization,null);

        // TODO take care of other types of SubstitutionNodes
        SingleAssignSubstitution initialization = (SingleAssignSubstitution) machine.getInitialisation();
        ExprNode initialValue = initialization.getValue();

        Expr initialValueAsZ3Expression = translator.translateExpression(initialValue);
        Sort z3TypeOfInitialValue = translator.bTypeToZ3Sort(initialValue.getType());

        Expr theIdentifier = ctx.mkConst(initialization.getIdentifier().getName(), z3TypeOfInitialValue);

        BoolExpr initialValueConstraint = ctx.mkEq(theIdentifier, initialValueAsZ3Expression);

        Map<String, Expr> initialStateValue = new HashMap<>();

        BoolExpr invariant;

        // prepare initial state value
        initialStateValue.put(initialization.getIdentifier().getName(), initialValueConstraint);

        // insert initial state
        queue.add(new State(null, initialStateValue));

        // prepare invariant
        {
            PredicateNode invariantNode = machine.getInvariant();

            // TODO this is ugly, we need a top level method here!
            if (invariantNode instanceof PredicateOperatorNode) {
                invariant = (BoolExpr) translator.visitPredicateOperatorNode((PredicateOperatorNode) invariantNode, null);
            } else if (invariantNode instanceof PredicateOperatorWithExprArgsNode) {
                invariant = (BoolExpr) translator.visitPredicateOperatorWithExprArgs((PredicateOperatorWithExprArgsNode) invariantNode, null);
            } else {
                throw new AssertionError("Invariant generating not implemented for: " + invariantNode.getClass());
            }

            solver.add(invariant);

            if (solver.check() != Status.SATISFIABLE) {
                throw new AssertionError("Invariant not satisfiable:" + invariant);
            }
        }

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // check invariant

            // compute successors
            // add to queue if not in visited

        }

        return false;// TODO
    }
}
