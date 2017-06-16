package de.bmoth.modelchecker.bmc;

import de.bmoth.modelchecker.State;

public class BoundedModelCheckingResult {
    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS
    }

    private final State lastState;
    private final int steps;
    private final Type type;

    private BoundedModelCheckingResult(State lastState, int steps, Type type) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
    }


    static BoundedModelCheckingResult createCounterExampleFound(State lastState, int steps) {
        return new BoundedModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND);
    }

    static BoundedModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new BoundedModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS);
    }

    public State getLastState() {
        return lastState;
    }

    public Type getType() {
        return type;
    }

    public int getSteps() {
        return steps;
    }
}
