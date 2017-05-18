package de.bmoth.modelchecker;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelCheckerTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testAnySubstitution() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x:NATURAL & y : NATURAL \n";
        machine += "INITIALISATION x,y:= 1,2 \n";
        machine += "OPERATIONS\n";
        machine += "\treplaceBoth =\n";
        machine += "\t\tANY nVal \n";
        machine += "\t\tWHERE nVal:1..3\n";
        machine += "\t\tTHEN x,y := nVal,nVal\n";
        machine += "\tEND\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertTrue(result.isCorrect());
    }

    @Test
    public void testAnySubstitutionWithInvariantViolation() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x < 4 \n";
        machine += "INITIALISATION x := 1 \n";
        machine += "OPERATIONS\n";
        machine += "\treplaceX =\n";
        machine += "\t\tANY nVal \n";
        machine += "\t\tWHERE nVal > 0 & nVal < 5\n"; // should be < 4 to avoid violation
        machine += "\t\tTHEN x := nVal\n";
        machine += "\tEND\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertFalse(result.isCorrect());
    }

    @Test
    public void testSimpleMachineWithOperations() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL & x >= 0 & x <= 2\n";
        machine += "INITIALISATION x := 0\n";
        machine += "OPERATIONS\n";
        machine += "\tInc = SELECT x < 2 THEN x := x + 1 END;\n";
        machine += "\tDec = SELECT x > 0 THEN x := x - 1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertTrue(result.isCorrect());
    }

    @Test
    public void testSimpleMachineWithOperations2() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL & x >= 0 & x <= 2\n";
        machine += "INITIALISATION x := 0\n";
        machine += "OPERATIONS\n";
        machine += "\tBlockSubstitution = BEGIN x := x + 1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        // the operation BlockSubstitution will finally violate the invariant x<=2
        assertFalse(result.isCorrect());
    }
}
