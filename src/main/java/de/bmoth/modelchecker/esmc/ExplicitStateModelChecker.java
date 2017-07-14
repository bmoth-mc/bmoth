package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.StateSpaceNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomaton;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;

import static de.bmoth.modelchecker.ModelCheckingResult.*;

public class ExplicitStateModelChecker extends ModelChecker {
    private Solver solver;
    private Solver opSolver;
    private SolutionFinder finder;
    private SolutionFinder opFinder;
    private Set<State> visited;
    private Queue<State> queue;
    private Map<State, StateSpaceNode> knownStateToStateSpaceNode;
    private Set<StateSpaceNode> stateSpace;
    private BuechiAutomaton buechiAutomaton;

    public ExplicitStateModelChecker(MachineNode machine) {
        super(machine);
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.opSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());
        this.opFinder = new SolutionFinder(opSolver, getContext());
        this.knownStateToStateSpaceNode = new HashMap<>();
        this.buechiAutomaton = new BuechiAutomaton();
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
    protected ModelCheckingResult doModelCheck() {
        final int maxInitialStates = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE);
        final int maxTransitions = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS);

        visited = new HashSet<>();
        queue = new LinkedList<>();
        stateSpace = new HashSet<>();

        // prepare initial states
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint, maxInitialStates);
        models.stream()
            .map(this::getStateFromModel)
            .forEach(state -> {
                updateStateSpace(null, state);
                stateSpace.add(knownStateToStateSpaceNode.get(state));
            });

        final BoolExpr invariant = getMachineTranslator().getInvariantConstraint();
        solver.add(invariant);

        // create joint operations constraint and permanently add to separate
        // solver
        final BoolExpr operationsConstraint = getMachineTranslator().getCombinedOperationConstraint();
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
                    return createUnknown(visited.size(), solver.getReasonUnknown());
                case UNSATISFIABLE:
                    return createCounterExampleFound(visited.size(), current);
                case SATISFIABLE:
                default:
                    // continue
            }

            // compute successors on separate finder
            models = opFinder.findSolutions(stateConstraint, maxTransitions);
            models.stream()
                .map(model -> getStateFromModel(current, model))
                .forEach(successor -> updateStateSpace(current, successor));

            solver.pop();
        }

        if (isAborted()) {
            return createAborted(visited.size());
        } else {
            // TODO think about state space root!
            return createVerified(visited.size(), stateSpace);
        }
    }

    private void updateStateSpace(State from, State to) {
        StateSpaceNode toNode;

        if (!knownStateToStateSpaceNode.containsKey(to)) {
            toNode = new StateSpaceNode(to);
            knownStateToStateSpaceNode.put(to, toNode);
            // !queue.contains(...) check can be omitted as it is always parallel to insertion into knownStateToStateSpaceNode
            if (!visited.contains(to)){
                queue.add(to);
            }
        } else {
            toNode = knownStateToStateSpaceNode.get(to);
        }

        if (from != null) {
            StateSpaceNode fromNode = knownStateToStateSpaceNode.get(from);
            fromNode.addSuccessor(toNode);
        }
    }

    private State getStateFromModel(Model model) {
        return getStateFromModel(null, model, TranslationOptions.PRIMED_0, buechiAutomaton);
    }

    private State getStateFromModel(State predecessor, Model model) {
        return getStateFromModel(predecessor, model, TranslationOptions.PRIMED_0, buechiAutomaton);
    }
}
