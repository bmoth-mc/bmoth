package de.bmoth.modelchecker;

public class ModelCheckingResult {

    private final int steps;
    private final State lastState;
    private final Type type;
    private final String reason;

    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS,
        VERIFIED,
        ABORTED,
        UNKNOWN
    }

    private ModelCheckingResult(State lastState, int steps, Type type, String reason) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
        this.reason = reason;
    }

    public static ModelCheckingResult createVerified(int steps) {
        return new ModelCheckingResult(null, steps, Type.VERIFIED, null);
    }

    public static ModelCheckingResult createAborted(int steps) {
        return new ModelCheckingResult(null, steps, Type.ABORTED, null);
    }

    public static ModelCheckingResult createUnknown(int steps, String reason) {
        return new ModelCheckingResult(null, steps, Type.UNKNOWN, reason);
    }

    public static ModelCheckingResult createCounterExampleFound(int steps, State lastState) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null);
    }

    public static ModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new ModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS, null);
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
