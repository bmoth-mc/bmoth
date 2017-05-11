package de.bmoth.modelchecker;

public class ModelCheckingResult {
    private Boolean correct;
    private State lastState;

    public ModelCheckingResult(String result){
        if (result.equals("correct")) {
            correct = true;
        }
    }

    public ModelCheckingResult(State state){
        correct = false;
        lastState = state;
    }

    public Boolean isCorrect() {
        return correct;
    }

    public String getLastState() {
        return lastState.values.toString();
    }
}
