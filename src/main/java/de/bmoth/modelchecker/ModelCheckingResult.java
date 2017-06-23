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

    static public ModelCheckingResult createVerified(int steps) {
        return new ModelCheckingResult(null, steps, Type.VERIFIED, null);
    }

    static public ModelCheckingResult createAborted(int steps) {
        return new ModelCheckingResult(null, steps, Type.ABORTED, null);
    }

    static public ModelCheckingResult createUnknown(int steps, String reason) {
        return new ModelCheckingResult(null, steps, Type.UNKNOWN, reason);
    }

    static public ModelCheckingResult createCounterExampleFound(int steps, State lastState) {
        return new ModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND, null);
    }

    static public ModelCheckingResult createExceededMaxSteps(int maxSteps) {
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
}
