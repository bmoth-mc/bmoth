package de.bmoth.modelchecker;

import com.microsoft.z3.*;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;

public class ExplicitStateModelChecker extends ModelChecker<ModelCheckingResult> {
    private Solver solver;
    private Solver opSolver;
    private SolutionFinder finder;
    private SolutionFinder opFinder;

    public ExplicitStateModelChecker(MachineNode machine) {
        super(machine);
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.opSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());
        this.opFinder = new SolutionFinder(opSolver, getContext());
    }

    public static ModelCheckingResult check(MachineNode machine) {
        ExplicitStateModelChecker modelChecker = new ExplicitStateModelChecker(machine);
        return modelChecker.check();
    }

    @Override
    public void abort() {
        super.abort();
        finder.abort();
        opFinder.abort();
    }

    @Override
    ModelCheckingResult doModelCheck() {
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        // prepare initial states
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint,
            BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE));
        models.stream().map(this::getStateFromModel).filter(s -> !queue.contains(s)).forEach(queue::add);

        final BoolExpr invariant = getMachineTranslator().getInvariantConstraint();
        solver.add(invariant);

        // create joint operations constraint and permanently add to separate
        // solver
        final BoolExpr operationsConstraint = getContext()
            .mkOr(getMachineTranslator().getOperationConstraints().toArray(new BoolExpr[0]));
        opSolver.add(operationsConstraint);

        while (!isAborted() && !queue.isEmpty()) {
            solver.push();
            State current = queue.poll();
            visited.add(current);

            // apply current state - remains stored in solver for loop iteration
            BoolExpr stateConstraint = current.getStateConstraint(getContext());
            solver.add(stateConstraint);

            // check invariant & state
            Status check = solver.check();
            switch (check) {
                case UNKNOWN:
                    return new ModelCheckingResult("check-sat = unknown, reason: " + solver.getReasonUnknown(),
                        visited.size());
                case UNSATISFIABLE:
                    return new ModelCheckingResult(current, visited.size());
                case SATISFIABLE:
                default:
                    // continue
            }

            // compute successors on separate finder
            models = opFinder.findSolutions(stateConstraint,
                BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS));
            models.stream().map(model -> getStateFromModel(current, model)).filter(state -> !visited.contains(state))
                .filter(state -> !queue.contains(state)).forEach(queue::add);

            solver.pop();
        }

        if (isAborted()) {
            return new ModelCheckingResult("aborted", visited.size());
        } else {
            return new ModelCheckingResult("correct", visited.size());
        }
    }

}
