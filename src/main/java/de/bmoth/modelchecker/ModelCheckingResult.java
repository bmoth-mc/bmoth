package de.bmoth.modelchecker;

import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModelCheckingResult {

    private final int steps;
    private final State lastState;
    private final Type type;
    private final String reason;
    private final StateSpace stateSpace;

    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        LTL_COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS,
        VERIFIED,
        ABORTED,
        UNKNOWN
    }

    private ModelCheckingResult(State lastState, int steps, Type type, String reason, StateSpace stateSpace) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
        this.reason = reason;
        this.stateSpace = stateSpace;
    }

    public static ModelCheckingResult createVerified(int steps, StateSpace stateSpace) {
        return new ModelCheckingResult(null, steps, Type.VERIFIED, null, stateSpace);
    }

    public static ModelCheckingResult createAborted(int steps) {
        return new ModelCheckingResult(null, steps, Type.ABORTED, null, null);
    }

    public static ModelCheckingResult createUnknown(int steps, String reason) {
        return new ModelCheckingResult(null, steps, Type.UNKNOWN, reason, null);
    }

    public static ModelCheckingResult createCounterExampleFound(int steps, State lastState, StateSpace stateSpace) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null, stateSpace);
    }

    public static ModelCheckingResult createLTLCounterExampleFound(int steps, State lastState) {
        return new ModelCheckingResult(lastState, steps, Type.LTL_COUNTER_EXAMPLE_FOUND, null, null);
    }

    public static ModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new ModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS, null, null);
    }

    public State getLastState() {
        return lastState;
    }

    public List<State> getCounterExamplePath() {
        if (stateSpace != null && lastState != null) {
            ShortestPathAlgorithm<State, DefaultEdge> pathFinder = new DijkstraShortestPath<>(stateSpace);

            Optional<List<State>> shortestPath = stateSpace.rootVertexSet().stream()
                .map(root -> pathFinder.getPath(root, lastState).getVertexList())
                .sorted((first, second) -> first.size() < second.size() ? 1 : -1)
                .findFirst();

            return shortestPath.get();
        } else {
            return Collections.emptyList();
        }
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
