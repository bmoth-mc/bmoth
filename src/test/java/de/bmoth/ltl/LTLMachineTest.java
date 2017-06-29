package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class LTLMachineTest extends TestParser {

    @Test
    public void testSimpleLTLTestMachine() {
        MachineNode machine = new MachineBuilder()
            .setName("SimpleLTLTestMachine")
            .setDefinitions("ASSERT_LTL_1 == \"F({x=7})\"")
            .setVariables("x")
            .setInvariant("x : 1..10")
            .setInitialization("x := 1")
            .addOperation("foo = SELECT x < 5 THEN x := x + 1 END")
            .build();

        assertNotNull(machine);
        List<LTLFormula> ltlFormulas = machine.getLTLFormulas();
        LTLFormula ltl1 = ltlFormulas.get(0);
        assertEquals("ASSERT_LTL_1", ltl1.getName());
    }
}
