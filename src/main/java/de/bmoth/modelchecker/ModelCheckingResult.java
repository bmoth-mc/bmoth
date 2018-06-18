package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class ModelCheckingResult {

    private final int steps;
    private final State lastState;
    private final Type type;
    private final String reason;
    private final StateSpace stateSpace;
    private final List<State> counterExamplePath;

    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        LTL_COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS,
        VERIFIED,
        ABORTED,
        UNKNOWN
    }

    private ModelCheckingResult(State lastState, int steps, Type type, String reason, StateSpace stateSpace, List<State> counterExamplePath) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
        this.reason = reason;
        this.stateSpace = stateSpace;
        this.counterExamplePath = counterExamplePath;
    }

    public static ModelCheckingResult createVerified(int steps, StateSpace stateSpace) {
        return new ModelCheckingResult(null, steps, Type.VERIFIED, null, stateSpace, null);
    }

    public static ModelCheckingResult createAborted(int steps) {
        return new ModelCheckingResult(null, steps, Type.ABORTED, null, null, null);
    }

    public static ModelCheckingResult createUnknown(int steps, String reason) {
        return new ModelCheckingResult(null, steps, Type.UNKNOWN, reason, null, null);
    }

    public static ModelCheckingResult createCounterExampleFound(int steps, State lastState, StateSpace stateSpace) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null, stateSpace, findCounterExamplePath(stateSpace, lastState));
    }

    public static ModelCheckingResult createCounterExampleFound(int steps, State lastState, Model model) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null, null, findCounterExamplePath(model));
    }

    public static ModelCheckingResult createLTLCounterExampleFound(int steps, State lastState) {
        return new ModelCheckingResult(lastState, steps, Type.LTL_COUNTER_EXAMPLE_FOUND, null, null, null);
    }

    public static ModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new ModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS, null, null, null);
    }

    public State getLastState() {
        return lastState;
    }

    private static List<State> findCounterExamplePath(StateSpace stateSpace, State lastState) {
        if (stateSpace != null && lastState != null) {
            ShortestPathAlgorithm<State, DefaultEdge> pathFinder = new DijkstraShortestPath<>(stateSpace);
            Optional<List<State>> shortestPath = stateSpace.rootVertexSet().stream()
                .filter(root -> (pathFinder.getPath(root, lastState) != null))
                .map(root -> pathFinder.getPath(root, lastState).getVertexList())
                .min((first, second) -> first.size() < second.size() ? 1 : -1);

            return shortestPath.orElseGet(Collections::emptyList);
        } else {
            return Collections.emptyList();
        }
    }

    private static List<State> findCounterExamplePath(Model model) {
        List<State> path = new ArrayList<>();
        HashMap<Integer, HashMap<String, Expr>> states = new HashMap<>();
        for (FuncDecl decl: model.getDecls()) {
            String name = decl.getName().toString();
            Expr value = model.getConstInterp(decl);
            if (name.contains("'")) {
                int index = Integer.parseInt(name.split("'")[1]);
                HashMap<String, Expr> indexedStates = states.containsKey(index) ? states.get(index) : new HashMap<>();
                indexedStates.put(name.split("'")[0], value);
                states.put(index, indexedStates);
            }
        }

        for (HashMap.Entry<Integer, HashMap<String, Expr>> entry : states.entrySet())
        {
            path.add(new State(entry.getValue()));
        }
        return path;
    }

    public Type getType() {
        return type;
    }

    public boolean isCorrect() {
        return type == Type.VERIFIED;
    }

    public StateSpace getStateSpace() {
        return stateSpace;
    }

    public int getSteps() {
        return steps;
    }

    public String getReason() {
        return reason;
    }

    public List<State> getCounterExamplePath() {
        return counterExamplePath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.name()).append(' ');

        switch (type) {
            case COUNTER_EXAMPLE_FOUND:
                sb.append(lastState.toString()).append(' ');
                break;
            case UNKNOWN:
                sb.append(reason).append(' ');
                break;
            case LTL_COUNTER_EXAMPLE_FOUND:
                sb.append(lastState.toString()).append(' ');
                break;
            case EXCEEDED_MAX_STEPS:
            case VERIFIED:
            case ABORTED:
        }

        return sb.append("after ").append(steps).append(" steps").toString();
    }
}
