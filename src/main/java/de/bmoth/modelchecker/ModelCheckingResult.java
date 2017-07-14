package de.bmoth.modelchecker;

import java.util.Collections;
import java.util.Set;

public class ModelCheckingResult {

    private final int steps;
    private final State lastState;
    private final Type type;
    private final String reason;
    private final Set<StateSpaceNode> stateSpaceRoot;

    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS,
        VERIFIED,
        ABORTED,
        UNKNOWN
    }

    private ModelCheckingResult(State lastState, int steps, Type type, String reason, Set<StateSpaceNode> stateSpaceRoot) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
        this.reason = reason;
        this.stateSpaceRoot = stateSpaceRoot;
    }

    public static ModelCheckingResult createVerified(int steps, Set<StateSpaceNode> stateSpaceRoot) {
        if (stateSpaceRoot == null) {
            return new ModelCheckingResult(null, steps, Type.VERIFIED, null, Collections.emptySet());
        }
        return new ModelCheckingResult(null, steps, Type.VERIFIED, null, stateSpaceRoot);
    }

    public static ModelCheckingResult createAborted(int steps) {
        return new ModelCheckingResult(null, steps, Type.ABORTED, null, Collections.emptySet());
    }

    public static ModelCheckingResult createUnknown(int steps, String reason) {
        return new ModelCheckingResult(null, steps, Type.UNKNOWN, reason, Collections.emptySet());
    }

    public static ModelCheckingResult createCounterExampleFound(int steps, State lastState) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null, Collections.emptySet());
    }

    public static ModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new ModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS, null, Collections.emptySet());
    }

    public State getLastState() {
        return lastState;
    }

    public Type getType() {
        return type;
    }

    public boolean isCorrect() {
        return type == Type.VERIFIED;
    }

    public Set<StateSpaceNode> getStateSpaceRoot() {
        return stateSpaceRoot;
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
            case EXCEEDED_MAX_STEPS:
            case VERIFIED:
            case ABORTED:
        }

        return sb.append("after ").append(steps).append(" steps").toString();
    }
}
