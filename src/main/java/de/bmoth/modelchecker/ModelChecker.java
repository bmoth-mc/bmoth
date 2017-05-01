package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.MachineToZ3Translator;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

/**
 * Created by krings on 28.04.17.
 */
public class ModelChecker {
    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        FormulaToZ3Translator translator = new FormulaToZ3Translator(ctx);
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();

        // prepare initial state
        Map<String, Expr> initialStateValue = new HashMap<>();
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();
        initialStateValue.put("Initial", initialValueConstraint);

        // insert initial state
        queue.add(new State(null, initialStateValue));

        // prepare invariant
        BoolExpr invariant;
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
        }

        solver.add(invariant);

        if (solver.check() != Status.SATISFIABLE) {
            throw new AssertionError("Invariant not satisfiable:" + invariant);
        }

        while (!queue.isEmpty()) {
            State current = queue.poll();
            BoolExpr stateValues = current.getValuesExpression(ctx);

            solver.push();
            solver.add(stateValues);

            // check invariant
            if (solver.check() == Status.SATISFIABLE) {
                solver.pop();
                // compute successors
                // add to queue if not in visited
            } else {
                throw new AssertionError("State values invalid: " + stateValues);
                //return false;
            }
        }

        return false;// TODO
    }

    private static State getStateFromModel(State predecessor, Model model, MachineToZ3Translator machineTranslator) {
        HashMap<String, Expr> map = new HashMap<>();
        for (DeclarationNode declNode : machineTranslator.getVariables()) {
            Expr expr = machineTranslator.getPrimedVariable(declNode);
            Expr value = model.eval(expr, true);
            map.put(declNode.getName(), value);
        }
        State newState = new State(predecessor, map);
        return newState;
    }
}
