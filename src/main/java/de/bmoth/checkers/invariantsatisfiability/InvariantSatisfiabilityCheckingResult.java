package de.bmoth.checkers.invariantsatisfiability;

import com.microsoft.z3.Status;

public class InvariantSatisfiabilityCheckingResult {
    private Status result;

    InvariantSatisfiabilityCheckingResult(Status z3Status) {
        this.result = z3Status;
    }

    public Status getResult() {
        return result;
    }

}
