package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.*;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.ltl.*;
import de.bmoth.preferences.BMothPreferences;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

import static de.bmoth.modelchecker.ModelCheckingResult.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ExplicitStateModelChecker extends ModelChecker {
    private Solver solver;
    private Solver opSolver;
    private Solver labelSolver;
    private SolutionFinder finder;
    private SolutionFinder opFinder;
    private Set<State> visited;
    private Queue<State> queue;
    private BuechiAutomaton buechiAutomaton;
    private StateSpace stateSpace;

    public ExplicitStateModelChecker(MachineNode machine) {
        super(machine);
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.opSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.labelSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());
        this.opFinder = new SolutionFinder(opSolver, getContext());
        List<LTLFormula> ltlFormulas = machine.getLTLFormulas();
        if (ltlFormulas.size() == 1) {
            LTLNode negatedFormula = new LTLPrefixOperatorNode(NOT, ltlFormulas.get(0).getLTLNode());
            this.buechiAutomaton = new BuechiAutomaton(LTLTransformations.transformLTLNode(negatedFormula));
        } else {
            this.buechiAutomaton = null;
        }
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

        stateSpace = new StateSpace();

        visited = new HashSet<>();
        queue = new LinkedList<>();

        // prepare initial states
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint, maxInitialStates);
        models.stream()
            .map(this::getStateFromModel).filter(this::isUnknown)
            .forEach(root -> {
                stateSpace.addRootVertex(root);
                queue.add(root);
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
                .forEach(successor -> {
                        if (isUnknown(successor)) {
                            stateSpace.addVertex(successor);
                            queue.add(successor);
                        }
                        stateSpace.addEdge(current, successor);
                    }
                );

            solver.pop();
        }

        if (isAborted()) {
            return createAborted(visited.size());
        } else {
            //ModelCheckingResult resultVerified = createVerified(visited.size(), _stateSpaceRoot);
            ModelCheckingResult resultVerified = createVerified(visited.size(), stateSpace);

            if (buechiAutomaton != null) {
                // do ltl model check
                labelStateSpace();
                List<List<State>> cycles = new TarjanSimpleCycles<>(stateSpace).findSimpleCycles();
                for (List<State> cycle : cycles) {
                    // if there is an accepting Buechi state in the cycle, a counterexample is found
                    for (State state : cycle) {
                        if (buechiAutomaton.isAcceptingSet(state.getBuechiNodes())) {
                            return createLTLCounterExampleFound(visited.size(), state);
                        }
                    }
                }
            }
            return resultVerified;
        }
    }

    private void labelStateSpace() {
        Queue<State> statesToUpdate = new ArrayDeque<>();
        statesToUpdate.addAll(stateSpace.vertexSet());
        while (!statesToUpdate.isEmpty()) {
            State current = statesToUpdate.poll();
            final Set<BuechiAutomatonNode> buechiNodes = new HashSet<>();
            final Set<BuechiAutomatonNode> candidates = new HashSet<>();
            // cant check for inDegreeOf as there might be a loop in the state space
            //if (current.getPredecessor() == null) {
            if (stateSpace.rootVertexSet().contains(current)) {
                candidates.addAll(buechiAutomaton.getInitialStates());
            } else {
                Set<DefaultEdge> incomingEdges = stateSpace.incomingEdgesOf(current);
                for (DefaultEdge incomingEdge : incomingEdges) {
                    State predecessor = stateSpace.getEdgeSource(incomingEdge);
                    predecessor.getBuechiNodes().forEach(n -> candidates.addAll(n.getSuccessors()));
                }
            }
            for (BuechiAutomatonNode node : candidates) {
                if (node.getLabels().isEmpty()) {
                    buechiNodes.add(node);
                }
                // TODO use all labels?
                for (PredicateNode label : node.getLabels()) {
                    labelSolver.reset();
                    labelSolver.add(FormulaToZ3Translator.translatePredicate(label, getContext(), getMachineTranslator().getZ3TypeInference()));
                    labelSolver.add(current.getStateConstraint(getContext()));
                    Status status = labelSolver.check();
                    switch (status) {
                        case UNSATISFIABLE:
                            break;
                        case UNKNOWN:
                            throw new UnsupportedOperationException("should not be undefined");
                        case SATISFIABLE:
                            buechiNodes.add(node);
                    }
                }
            }

            buechiNodes.stream().filter(n -> !current.getBuechiNodes().contains(n)).forEach(newBuechiNode -> {
                // found a new node, need to update successors again
                current.addBuechiNode(newBuechiNode);

                Set<DefaultEdge> outgoingEdges = stateSpace.outgoingEdgesOf(current);
                for (DefaultEdge outgoingEdge : outgoingEdges) {
                    State successor = stateSpace.getEdgeTarget(outgoingEdge);
                    if (!statesToUpdate.contains(successor)) {
                        statesToUpdate.add(successor);
                    }
                }
            });
        }
    }

    private State getStateFromModel(Model model) {
        return getStateFromModel(null, model, TranslationOptions.PRIMED_0);
    }

    private State getStateFromModel(State predecessor, Model model) {
        return getStateFromModel(predecessor, model, TranslationOptions.PRIMED_0);
    }

    private boolean isUnknown(State state) {
        return !stateSpace.containsVertex(state) && !visited.contains(state);
    }
}
