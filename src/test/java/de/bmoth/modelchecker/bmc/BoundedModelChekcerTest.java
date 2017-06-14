package de.bmoth.modelchecker.bmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.BoundedModelChecker;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoundedModelChekcerTest extends TestParser {

    @Test
    @Ignore
    public void test1() {
        String machine = "MACHINE infInc\n" +
            "VARIABLES c\n" +
            "INVARIANT c : NATURAL\n" +
            "INITIALISATION c := 0\n" +
            "OPERATIONS\n" +
            "\tinc = BEGIN c := c + 1 END\n" +
            "END\n";

        BoundedModelChecker bmc = new BoundedModelChecker(parseMachine(machine), 20);
        assertTrue(bmc.check().booleanValue());
    }

    @Test
    @Ignore
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

        BoundedModelChecker bmc = new BoundedModelChecker(parseMachine(machine), 20);
        assertFalse(bmc.check().booleanValue());
    }
}
