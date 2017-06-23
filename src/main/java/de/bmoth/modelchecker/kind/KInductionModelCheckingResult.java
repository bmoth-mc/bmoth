package de.bmoth.modelchecker.kind;

import de.bmoth.modelchecker.State;

public class KInductionModelCheckingResult {
    public enum Type {
        COUNTER_EXAMPLE_FOUND,
        EXCEEDED_MAX_STEPS,
        CORRECT
    }

    private final State lastState;
    private final int steps;
    private final Type type;

    private KInductionModelCheckingResult(State lastState, int steps, Type type) {
        this.lastState = lastState;
        this.steps = steps;
        this.type = type;
    }


    static KInductionModelCheckingResult createCounterExampleFound(State lastState, int steps) {
        return new KInductionModelCheckingResult(lastState, steps, Type.COUNTER_EXAMPLE_FOUND);
    }

    static KInductionModelCheckingResult createExceededMaxSteps(int maxSteps) {
        return new KInductionModelCheckingResult(null, maxSteps, Type.EXCEEDED_MAX_STEPS);
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
