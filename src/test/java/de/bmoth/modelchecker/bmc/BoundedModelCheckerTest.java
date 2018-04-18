package de.bmoth.modelchecker.bmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.COUNTER_EXAMPLE_FOUND;
import static de.bmoth.modelchecker.ModelCheckingResult.Type.EXCEEDED_MAX_STEPS;
import static org.junit.Assert.assertEquals;

public class BoundedModelCheckerTest extends TestParser {

    private MachineBuilder builder;
    private MachineNode machine;
    private ModelCheckingResult result;

    @Before
    public void init() {
        builder = new MachineBuilder();
        machine = null;
        result = null;
    }

    @Test
    public void test1() {
        machine = builder
            .setName("infInc")
            .setVariables("c")
            .setInvariant("c : NATURAL")
            .setInitialization("c := 0")
            .addOperation("inc = BEGIN c := c + 1 END")
            .build();

        result = new BoundedModelChecker(machine, 20).check();
        assertEquals(EXCEEDED_MAX_STEPS, result.getType());
        assertEquals(20, result.getSteps());
    }

    @Test
    public void test2() {
        machine = builder
            .setName("ebr")
            .setVariables("c, b")
            .setInvariant("c : INTEGER & b = TRUE")
            .setInitialization("c := 0 || b := TRUE")
            .addOperation("inc = ANY x WHERE x:INTEGER THEN c := c + x END")
            .addOperation("err = PRE c > 99999 THEN b := FALSE END")
            .build();

        result = new BoundedModelChecker(machine, 20).check();
        assertEquals(COUNTER_EXAMPLE_FOUND, result.getType());
        assertEquals("{b=false, c=100000}", result.getLastState().toString());
        assertEquals(2, result.getSteps());
    }

    @Test
    public void testExponentiation() {
        machine = builder
            .setName("Exponentiation")
            .setVariables("x")
            .setInvariant("x=2 & 2**0 = 1")
            .setInitialization("x := 2")
            .build();

        result = new BoundedModelChecker(machine, 5).check();
        assertEquals(EXCEEDED_MAX_STEPS, result.getType());
        assertEquals(5, result.getSteps());
    }
}
