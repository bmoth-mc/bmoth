package de.bmoth.issues;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Issue59Test {
    @Test
    public void testIssue59() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : INTEGER &\n";
        machine += "\tx**2 = x*x \n";
        machine += "INITIALISATION x := -3\n";
        machine += "OPERATIONS\n";
        machine += "\tIncX = SELECT x < 50 THEN x := x+1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testIssue59WithAdditionalInvariant() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : INTEGER &\n";
        machine += "\tx**2 = x*x &\n";
        machine += "\t#x.(x:INTEGER & {x} \\/ {1,2} = {1,2})\n";
        machine += "INITIALISATION x := -3\n";
        machine += "OPERATIONS\n";
        machine += "\tIncX = SELECT x < 50 THEN x := x+1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
        assertEquals(true, result.getMessage().startsWith("check-sat"));
    }
}
