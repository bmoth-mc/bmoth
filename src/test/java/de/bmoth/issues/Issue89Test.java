package de.bmoth.issues;

import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.esmc.ModelCheckingResult;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static de.bmoth.TestParser.*;

public class Issue89Test {
    @Test
    public void testPinDownPowProblem() {
        String machine = "MACHINE PinDownPowProblem\n";
        machine += "VARIABLES\n";
        machine += "\tx, y\n";
        machine += "INVARIANT\n";
        machine += "\tx : INTEGER\n";
        machine += "\t& y : INTEGER\n";
        machine += "\t& x < 100\n";
        machine += "INITIALISATION\n";
        machine += "\tx := 10**5\n";
        machine += "\t|| y := 2**32-1\n";
        machine += "OPERATIONS\n";
        machine += "inc = BEGIN x := 2 * x END\n" + "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertFalse(result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());
        assertEquals("{x=100000, y=4294967295}", result.getLastState().toString());
    }
}
