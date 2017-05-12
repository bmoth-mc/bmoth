package de.bmoth.modelchecker;

import java.util.ArrayList;
import java.util.List;

public class ModelCheckingResult {
    private Boolean correct;
    private State lastState;

    ModelCheckingResult(String result) {
        if (result.equals("correct")) {
            correct = true;
        }
    }

    ModelCheckingResult(State state) {
        correct = false;
        lastState = state;
    }

    public Boolean isCorrect() {
        return correct;
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
