package de.bmoth.modelchecker;

import java.util.ArrayList;
import java.util.List;

public class ModelCheckingResult {
    private Boolean correct;
    private String message = "";
    private State lastState;

    ModelCheckingResult(String result) {
        if (result.equals("correct")) {
            correct = true;
        } else if (result.startsWith("check-sat")) {
            message = result;
        }
    }

    ModelCheckingResult(State state) {
        correct = false;
        lastState = state;
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
        while (state.predecessor != null) {
            path.add(state.predecessor.toString());
            state = state.predecessor;
        }
        return path;
    }
}
