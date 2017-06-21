package de.bmoth.modelchecker.kind;

import de.bmoth.modelchecker.State;

public class KinductionModelCheckingResult {
    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS
    }

    private final State lastState;
    private final int steps;
    private final Type type;

    private KinductionModelCheckingResult(State lastState, int steps, Type type) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
    }


    static KinductionModelCheckingResult createCounterExampleFound(State lastState, int steps) {
        return new KinductionModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND);
    }

    static KinductionModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new KinductionModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS);
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
