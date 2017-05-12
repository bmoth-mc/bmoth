package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.app.PersonalPreference;
import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.MachineToZ3Translator;
import de.bmoth.backend.SolutionFinder;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

public class ModelChecker {
    static private PersonalPreference pp;
    public static ModelCheckingResult doModelCheck(String machineAsString, PersonalPreference personalPreference) {
        pp=personalPreference;
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doModelCheck(machineAsSemanticAst);
    }

    public static ModelCheckingResult doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();

        // prepare initial states
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();
        SolutionFinder finder = new SolutionFinder(initialValueConstraint, solver, ctx);
        Set<Model> models = finder.findSolutions(pp.getMaxInitialStates());
        for (Model model : models) {
            State state = getStateFromModel(null, model, machineTranslator);
            queue.add(state);
        }

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        while (!queue.isEmpty()) {
            solver.push();
            State current = queue.poll();
            // apply current state - remains stored in server for loop iteration
            BoolExpr stateConstraint = current.getStateConstraint(ctx);
            solver.add(stateConstraint);
            // check invariant
            solver.push();
            solver.add(invariant);
            Status check = solver.check();
            solver.pop();
            if (check != Status.SATISFIABLE) {
                return new ModelCheckingResult(current);
            }
            visited.add(current);

            List<BoolExpr> operationConstraints = machineTranslator.getOperationConstraints();
            for (BoolExpr currentOperationConstraint : operationConstraints) {
                // compute successors
                finder = new SolutionFinder(currentOperationConstraint, solver, ctx);
                models = finder.findSolutions(pp.getMaxSolution());
                for (Model model : models) {
                    State state = getStateFromModel(current, model, machineTranslator);
                    // add to queue if not in visited
                    if (!visited.contains(state)) {
                        queue.add(state);
                    }
                }
            }
            solver.pop();
        }

        return new ModelCheckingResult("correct");

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
