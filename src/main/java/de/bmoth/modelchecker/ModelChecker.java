package de.bmoth.modelchecker;

import com.microsoft.z3.*;
import de.bmoth.app.PersonalPreferences;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.*;

public class ModelChecker {
    private ModelChecker() {
        // prevent instantiation
    }

    public static ModelCheckingResult doModelCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doModelCheck(machineAsSemanticAst);
    }

    public static ModelCheckingResult doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = Z3SolverFactory.getZ3Solver(ctx);
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        // prepare initial states
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();

        SolutionFinder finder = new SolutionFinder(initialValueConstraint, solver, ctx);
        Set<Model> models = finder.findSolutions(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INITIAL_STATE));
        for (Model model : models) {
            State state = getStateFromModel(null, model, machineTranslator);
            queue.add(state);
        }

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        while (!queue.isEmpty()) {
            solver.push();
            State current = queue.poll();

            // apply current state - remains stored in solver for loop iteration
            BoolExpr stateConstraint = current.getStateConstraint(ctx);
            solver.add(stateConstraint);

            // check invariant
            // TODO add invariant before entering while loop
            solver.push();
            solver.add(invariant);
            Status check = solver.check();
            solver.pop();
            switch (check) {
                case UNKNOWN:
                    return new ModelCheckingResult("check-sat = unknown, reason: " + solver.getReasonUnknown());
                case UNSATISFIABLE:
                    return new ModelCheckingResult(current);
                case SATISFIABLE:
                default:
                    // continue
            }
            visited.add(current);

            List<BoolExpr> operationConstraints = machineTranslator.getOperationConstraints();
            for (BoolExpr currentOperationConstraint : operationConstraints) {
                // compute successors
                finder = new SolutionFinder(currentOperationConstraint, solver, ctx);
                models = finder.findSolutions(PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_TRANSITIONS));
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
        for (DeclarationNode declarationNode : machineTranslator.getConstants()) {
            Expr expr = machineTranslator.getVariable(declarationNode);
            Expr value = model.eval(expr, true);
            map.put(declarationNode.getName(), value);
        }

        State newState = new State(predecessor, map);
        return newState;
    }
}
