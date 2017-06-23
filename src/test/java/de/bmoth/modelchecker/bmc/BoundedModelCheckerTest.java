package de.bmoth.modelchecker.bmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.COUNTER_EXAMPLE_FOUND;
import static de.bmoth.modelchecker.ModelCheckingResult.Type.EXCEEDED_MAX_STEPS;
import static org.junit.Assert.assertEquals;

public class BoundedModelCheckerTest extends TestParser {

    @Test
    public void test1() {
        String machine = "MACHINE infInc\n" +
            "VARIABLES c\n" +
            "INVARIANT c : NATURAL\n" +
            "INITIALISATION c := 0\n" +
            "OPERATIONS\n" +
            "\tinc = BEGIN c := c + 1 END\n" +
            "END\n";

        ModelCheckingResult result = new BoundedModelChecker(parseMachine(machine), 20).check();
        assertEquals(EXCEEDED_MAX_STEPS, result.getType());
        assertEquals(20, result.getSteps());
    }

    @Test
    public void test2() {
        String machine = "MACHINE ebr\n" +
            "VARIABLES c, b\n" +
            "INVARIANT c : INTEGER &\n" +
            "\tb = TRUE\n" +
            "INITIALISATION c := 0 || b := TRUE\n" +
            "OPERATIONS\n" +
            "\tinc = ANY x WHERE x:INTEGER THEN c := c + x END;\n" +
            "\terr = PRE c > 99999 THEN b := FALSE END\n" +
            "END\n";

        ModelCheckingResult result = new BoundedModelChecker(parseMachine(machine), 20).check();
        assertEquals(COUNTER_EXAMPLE_FOUND, result.getType());
        assertEquals("{b=false, c=100000}", result.getLastState().toString());
        assertEquals(2, result.getSteps());
    }
}
