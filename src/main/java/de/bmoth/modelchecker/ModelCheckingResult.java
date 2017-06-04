package de.bmoth.modelchecker;

import java.util.ArrayList;
import java.util.List;

public class ModelCheckingResult {
    private Boolean correct = false;
    private String message = "";
    private State lastState;
    private final int numberOfStatesVisited;

    ModelCheckingResult(String result, int numberOfStatesVisited) {
        this.numberOfStatesVisited = numberOfStatesVisited;
        if (result.equals("correct")) {
            correct = true;
        } else if (result.startsWith("check-sat") || result.equals("aborted")) {
            message = result;
        }
    }

    ModelCheckingResult(State state, int numberOfStatesVisited) {
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

    public static List<String> getPath(State state) {
        List<String> path = new ArrayList<>();
        for (State current = state.predecessor; current != null; current = current.predecessor) {
            path.add(current.toString());
        }
        return path;
    }
}
