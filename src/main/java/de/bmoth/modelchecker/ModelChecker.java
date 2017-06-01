package de.bmoth.modelchecker;

import com.microsoft.z3.*;
import de.bmoth.backend.Abortable;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;

public class ModelChecker implements Abortable {
    private Context ctx;
    private Solver solver;
    private Solver opSolver;
    private MachineToZ3Translator machineTranslator;
    private SolutionFinder finder;
    private SolutionFinder opFinder;
    private volatile boolean isAborted;

    public ModelChecker(MachineNode machine) {
        this.ctx = new Context();
        this.solver = Z3SolverFactory.getZ3Solver(ctx);
        this.opSolver = Z3SolverFactory.getZ3Solver(ctx);
        this.machineTranslator = new MachineToZ3Translator(machine, ctx);
        this.finder = new SolutionFinder(solver, ctx);
        this.opFinder = new SolutionFinder(opSolver, ctx);
    }

    public static ModelCheckingResult doModelCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doModelCheck(machineAsSemanticAst);
    }

    public static ModelCheckingResult doModelCheck(MachineNode machine) {
        ModelChecker modelChecker = new ModelChecker(machine);
        return modelChecker.doModelCheck();
    }

    @Override
    public void abort() {
        isAborted = true;
        finder.abort();
    }

    public ModelCheckingResult doModelCheck() {
        isAborted = false;
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        // prepare initial states
        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint, BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE));
        models.stream().map(this::getStateFromModel)
            .forEach(queue::add);

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        solver.add(invariant);

        // create joint operations constraint and permanently add to separate solver
        final BoolExpr operationsConstraint = ctx.mkOr(machineTranslator.getOperationConstraints().toArray(new BoolExpr[0]));
        opSolver.add(operationsConstraint);

        while (!isAborted && !queue.isEmpty()) {
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

            // compute successors on separate finder
            models = opFinder.findSolutions(stateConstraint, BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS));
            models.stream().map(model -> getStateFromModel(current, model))
                .filter(state -> !visited.contains(state))
                .filter(state -> !queue.contains(state))
                .forEach(queue::add);

            solver.pop();
        }

        if (isAborted) {
            return new ModelCheckingResult("aborted");
        } else {
            return new ModelCheckingResult("correct");
        }
    }

    private State getStateFromModel(Model model) {
        return getStateFromModel(null, model);
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
