package de.bmoth.modelchecker.sse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.StateSpaceNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;
import java.util.stream.Collectors;

import static de.bmoth.backend.TranslationOptions.PRIMED_0;
import static de.bmoth.modelchecker.ModelCheckingResult.createUnknown;

public class StateSpaceExplorator extends ModelChecker {
    private final Map<State, StateSpaceNode> knownStateToStateSpaceNode;
    private final Solver solver;
    private final SolutionFinder finder;
    private Set<StateSpaceNode> stateSpace;
    private Queue<State> queue;
    private int steps;


    public StateSpaceExplorator(MachineNode machine) {
        super(machine);
        this.knownStateToStateSpaceNode = new HashMap<>();
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());

    }

    @Override
    protected ModelCheckingResult doModelCheck() {
        // TODO use different values for sse then for esmc
        final int maxInitialStates = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE);
        final int maxTransitions = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS);

        stateSpace = new HashSet<>();
        queue = new LinkedList<>();
        steps = 0;

        initialize(maxInitialStates);

        final BoolExpr operationsConstraint = getMachineTranslator().getCombinedOperationConstraint();
        solver.add(operationsConstraint);

        while (!queue.isEmpty()) {
            solver.push();
            State current = queue.poll();

            BoolExpr stateConstraint = current.getStateConstraint(getContext());
            solver.add(stateConstraint);

            Status check = solver.check();
            switch (check) {
                case UNKNOWN:
                    return createUnknown(steps, solver.getReasonUnknown());
                case UNSATISFIABLE:
                    continue;
                case SATISFIABLE:
                default:
                    // continue
            }

            Set<State> successors = finder.findSolutions(stateConstraint, maxTransitions).stream().map(model -> getStateFromModel(null, model, PRIMED_0)).collect(Collectors.toSet());

            successors.forEach(successor -> updateStateSpace(current, successor));
            solver.pop();
        }


        return ModelCheckingResult.createVerified(steps, stateSpace);
    }

    private void updateStateSpace(State from, State to) {
        StateSpaceNode toNode;

        if (!knownStateToStateSpaceNode.containsKey(to)) {
            toNode = new StateSpaceNode(to);
            knownStateToStateSpaceNode.put(to, toNode);
            // !queue.contains(...) check can be omitted as it is always parallel to insertion into knownStateToStateSpaceNode
            queue.add(to);
        } else {
            toNode = knownStateToStateSpaceNode.get(to);
        }

        if (from != null) {
            StateSpaceNode fromNode = knownStateToStateSpaceNode.get(from);
            fromNode.addSuccessor(toNode);
        }

        steps++;
    }

    private void initialize(int maxInitialStates) {
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();
        Set<State> initialStates = finder.findSolutions(initialValueConstraint, maxInitialStates).stream()
            .map(model -> getStateFromModel(null, model, PRIMED_0)).collect(Collectors.toSet());

        initialStates.forEach(state -> updateStateSpace(null, state));
        initialStates.stream().map(knownStateToStateSpaceNode::get).forEach(stateSpace::add);
    }
}
