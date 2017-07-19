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
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.StateSpaceNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.ltl.*;
import de.bmoth.preferences.BMothPreferences;
import org.jgrapht.DirectedGraph;
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
    private Map<State, StateSpaceNode> knownStateToStateSpaceNode;
    private BuechiAutomaton buechiAutomaton;

    public ExplicitStateModelChecker(MachineNode machine) {
        super(machine);
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.opSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.labelSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());
        this.opFinder = new SolutionFinder(opSolver, getContext());
        this.knownStateToStateSpaceNode = new HashMap<>();
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

        visited = new HashSet<>();
        queue = new LinkedList<>();
        Set<StateSpaceNode> stateSpaceRoot = new HashSet<>();

        // prepare initial states
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint, maxInitialStates);
        models.stream()
            .map(this::getStateFromModel)
            .forEach(state -> {
                updateStateSpace(null, state);
                stateSpaceRoot.add(knownStateToStateSpaceNode.get(state));
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
            ModelCheckingResult resultVerified = createVerified(visited.size(), stateSpaceRoot);
            if (buechiAutomaton != null) {
                // do ltl model check
                labelStateSpace(resultVerified.getStateSpace().getGraph());
                List<List<State>> cycles = resultVerified.getStateSpace().getCycles();
                for (List<State> cycle : cycles) {
                    // if there is an accepting Buechi state in the cycle, a counterexample is found
                    for (State state : cycle) {
                        for (BuechiAutomatonNode node : state.getBuechiNodes()) {
                            if (node.isAccepting()) {
                                return createLTLCounterExampleFound(visited.size(), state);
                            }
                        }
                    }
                }
            }
            return resultVerified;
        }
    }
    

    private void updateStateSpace(State from, State to) {
        StateSpaceNode toNode;

        if (!knownStateToStateSpaceNode.containsKey(to)) {
            toNode = new StateSpaceNode(to);
            knownStateToStateSpaceNode.put(to, toNode);
            // !queue.contains(...) check can be omitted as it is always parallel to insertion into knownStateToStateSpaceNode
            if (!visited.contains(to)) {
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

    private void labelStateSpace(DirectedGraph<State, DefaultEdge> graph) {
        Queue<State> statesToUpdate = new ArrayDeque<>();
        statesToUpdate.addAll(graph.vertexSet());
        while (!statesToUpdate.isEmpty()) {
            State current = statesToUpdate.poll();
            final Set<BuechiAutomatonNode> buechiNodes = new HashSet<>();
            final Set<BuechiAutomatonNode> candidates = new HashSet<>();
            // cant check for inDegreeOf as there might be a loop in the state space
            if (current.getPredecessor() == null) {
                candidates.addAll(buechiAutomaton.getInitialStates());
            } else {
                Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(current);
                for (DefaultEdge incomingEdge : incomingEdges) {
                    State predecessor = graph.getEdgeSource(incomingEdge);
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

                Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(current);
                for (DefaultEdge outgoingEdge : outgoingEdges) {
                    State successor = graph.getEdgeTarget(outgoingEdge);
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
}
