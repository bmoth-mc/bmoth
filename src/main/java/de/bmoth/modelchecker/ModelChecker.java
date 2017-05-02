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
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();

        // prepare initial state
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();

        Solver solver = ctx.mkSolver();
        solver.add(initialValueConstraint);
        Status check = solver.check();
        if (check == Status.SATISFIABLE) {
            State state = getStateFromModel(null, solver.getModel(), machineTranslator);
            System.out.println(state);
            queue.add(state);
        } else {
            throw new AssertionError("Initial constraint state not satisfiable: " + initialValueConstraint);
        }

        // prepare invariant
        BoolExpr invariant = machineTranslator.getInvariantConstraint();

        solver.add(invariant);
        check = solver.check();
        if (check != Status.SATISFIABLE) {
            throw new AssertionError("Invariant not satisfiable:" + invariant);
        }

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // apply current state
            // check invariant

            // compute successors
            // add to queue if not in visited
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
