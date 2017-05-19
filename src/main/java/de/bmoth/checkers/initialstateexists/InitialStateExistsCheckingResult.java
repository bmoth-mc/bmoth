package de.bmoth.checkers.initialstateexists;

import com.microsoft.z3.Status;

public class InitialStateExistsCheckingResult {
    private Status result;

    InitialStateExistsCheckingResult(Status z3Status) {
        this.result = z3Status;
    }

    public Status getResult() {
        return result;
    }

}
