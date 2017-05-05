package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.MachineToZ3Translator;
import de.bmoth.backend.SolutionFinder;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

public class ModelChecker {

    public static boolean doModelCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doModelCheck(machineAsSemanticAst);
    }

    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();

        // prepare initial states
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();
        SolutionFinder finder = new SolutionFinder(initialValueConstraint, solver, ctx);
        Set<Model> models = finder.findSolutions(5);
        for (Model model : models) {
            State state = getStateFromModel(null, model, machineTranslator);
            queue.add(state);
        }

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        while (!queue.isEmpty()) {
            State current = queue.poll();
            // prepare invariant
            solver.reset();
            // apply current state
            BoolExpr stateConstraint = current.getStateConstraint(ctx);
            solver.add(stateConstraint);
            // check invariant
            solver.add(invariant);
            // check invariant
            Status check = solver.check();
            if (check != Status.SATISFIABLE) {
                return false;
            }
            visited.add(current);

            solver.reset();
            solver.add(stateConstraint);
            List<BoolExpr> constraints = machineTranslator.getOperationConstraints();
            for (BoolExpr boolExpr : constraints) {
                // compute successors
                finder = new SolutionFinder(boolExpr, solver, ctx);
                models = finder.findSolutions(5);
                for (Model model : models) {
                    State state = getStateFromModel(current, model, machineTranslator);
                    // add to queue if not in visited
                    if (!visited.contains(state)) {
                        queue.add(state);
                    }
                }
            }

        }

        return true;

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
