package de.bmoth.checkers.invariantsatisfiability;

import com.microsoft.z3.Status;

import de.bmoth.parser.Parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvariantSatisfiabilityCheckerTest {
    @Test
    public void testInvariantSat() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InvariantSatisfiabilityCheckingResult res = InvariantSatisfiabilityChecker
                .doInvariantSatisfiabilityCheck(Parser.getMachineAsSemanticAst(machine));
        assertEquals(Status.SATISFIABLE, res.getResult());
    }

    @Test
    public void testInvariantContradictory() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x=1 & x > 2 \n";
        machine += "INITIALISATION x:= 1 \n";
        machine += "END";

        InvariantSatisfiabilityCheckingResult res = InvariantSatisfiabilityChecker
                .doInvariantSatisfiabilityCheck(Parser.getMachineAsSemanticAst(machine));
        assertEquals(Status.UNSATISFIABLE, res.getResult());
    }
}
