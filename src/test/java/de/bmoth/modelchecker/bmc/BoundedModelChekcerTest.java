package de.bmoth.modelchecker.bmc;

import com.microsoft.z3.Expr;
import de.bmoth.TestParser;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

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

        Map<String, Expr> counterExample = new BoundedModelChecker(parseMachine(machine), 20).check();
        assertNull(counterExample);
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

        Map<String, Expr> counterExample = new BoundedModelChecker(parseMachine(machine), 20).check();
        assertEquals("{b=false, c=100000}", counterExample.toString());
    }
}
