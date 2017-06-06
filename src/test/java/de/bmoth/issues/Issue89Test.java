package de.bmoth.issues;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class Issue89Test {
    @Test
    public void testPinDownPowProblem() {
        String machine = "MACHINE PinDownPowProblem\n" +
            "VARIABLES\n" +
            "\tx, y\n" +
            "INVARIANT\n" +
            "\tx : INTEGER\n" +
            "\t& y : INTEGER\n" +
            "\t& x < 100\n" +
            "INITIALISATION\n" +
            "\tx := 10**5\n" +
            "\t|| y := 2**32-1\n" +
            "OPERATIONS\n" +
            "inc = BEGIN x := 2 * x END\n" +
            "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(Parser.getMachineAsSemanticAst(machine));
        assertFalse(result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());
        assertEquals("{x=100000, y=4294967295}", result.getLastState().toString());
    }
}
