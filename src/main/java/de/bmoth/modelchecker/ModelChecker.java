package de.bmoth.modelchecker;

import com.microsoft.z3.*;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;

public class ModelChecker {
    private Context ctx;
    private Solver solver;
    private MachineToZ3Translator machineTranslator;
    private SolutionFinder finder;

    private ModelChecker(MachineNode machine) {
        this.ctx = new Context();
        this.solver = Z3SolverFactory.getZ3Solver(ctx);
        this.machineTranslator = new MachineToZ3Translator(machine, ctx);
        this.finder = new SolutionFinder(solver, ctx);
    }

    public static ModelCheckingResult doModelCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doModelCheck(machineAsSemanticAst);
    }

    public static ModelCheckingResult doModelCheck(MachineNode machine) {
        ModelChecker modelChecker = new ModelChecker(machine);
        return modelChecker.doModelCheck();
    }

    private ModelCheckingResult doModelCheck() {
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        // prepare initial states
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint,BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE));
        for (Model model : models) {
            State state = getStateFromModel(null, model);
            queue.add(state);
        }

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        solver.add(invariant);
        while (!queue.isEmpty()) {
            solver.push();
            State current = queue.poll();

            // apply current state - remains stored in solver for loop iteration
            BoolExpr stateConstraint = current.getStateConstraint(ctx);
            solver.add(stateConstraint);

            // check invariant & state
            Status check = solver.check();
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
                models = finder.findSolutions(currentOperationConstraint,BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS));
                for (Model model : models) {
                    State state = getStateFromModel(current, model);

                    // add to queue if not in visited
                    if (!visited.contains(state) && !queue.contains(state)) {
                        queue.add(state);
                    }
                }
            }
            solver.pop();
        }

        return new ModelCheckingResult("correct");
    }

    private State getStateFromModel(State predecessor, Model model) {
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

        return new State(predecessor, map);
    }
}
