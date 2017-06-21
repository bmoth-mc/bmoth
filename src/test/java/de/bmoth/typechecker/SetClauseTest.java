package de.bmoth.typechecker;

import static de.bmoth.TestConstants.MACHINE_NAME;
import static de.bmoth.TestConstants.THREE_CONSTANTS;
import static de.bmoth.TestConstants.TWO_CONSTANTS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SetClauseTest {

    @Test
    public void testEnumeratedSet() {
        String machine =MACHINE_NAME;
        machine += "SETS S1 = {a, b, c}; S2 = {d, e} \n";
        machine += THREE_CONSTANTS;
        machine += "PROPERTIES k = a & k2 : S2 & k3 = S1 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);

        assertEquals("S1", t.getConstants().get("k"));
        assertEquals("S2", t.getConstants().get("k2"));
        assertEquals("POW(S1)", t.getConstants().get("k3"));
    }

    @Test
    public void testDeferredSet() {
        String machine = MACHINE_NAME;
        machine += "SETS S1; S2 \n";
        machine += TWO_CONSTANTS;
        machine += "PROPERTIES k : S1 & k2 = S2 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("S1", t.getConstants().get("k"));
        assertEquals("POW(S2)", t.getConstants().get("k2"));
    }
}
