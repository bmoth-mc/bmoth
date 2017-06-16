package de.bmoth.modelchecker.esmc;

import de.bmoth.modelchecker.State;

public class ModelCheckingResult {
    private Boolean correct = false;
    private String message = "";
    private State lastState;
    private final int numberOfStatesVisited;

    public ModelCheckingResult(String result, int numberOfStatesVisited) {
        this.numberOfStatesVisited = numberOfStatesVisited;
        if (result.equals("correct")) {
            correct = true;
        } else if (result.startsWith("check-sat") || result.equals("aborted")) {
            message = result;
        }
    }

    public ModelCheckingResult(State state, int numberOfStatesVisited) {
        this.numberOfStatesVisited = numberOfStatesVisited;
        lastState = state;
    }

    public int getNumberOfDistinctStatesVisited() {
        return this.numberOfStatesVisited;
    }

    public Boolean isCorrect() {
        return correct;
    }

    public String getMessage() {
        return message;
    }

    public State getLastState() {
        return lastState;
    }
}
