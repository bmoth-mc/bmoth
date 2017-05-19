package de.bmoth.checkers.invariantsatisfiability;

import com.microsoft.z3.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvariantSatisfiabilityCheckerTest {
    @Test
    public void testInvariantSat() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InvariantSatisfiabilityCheckingResult res = InvariantSatisfiabilityChecker.doInvariantSatisfiabilityCheck(machine);
        assertEquals(Status.SATISFIABLE, res.getResult());
    }

    @Test
    public void testInvariantContradictory() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 & x > 2 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InvariantSatisfiabilityCheckingResult res = InvariantSatisfiabilityChecker.doInvariantSatisfiabilityCheck(machine);
        assertEquals(Status.UNSATISFIABLE, res.getResult());
    }
}
