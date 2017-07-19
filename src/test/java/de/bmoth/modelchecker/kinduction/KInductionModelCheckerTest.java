package de.bmoth.modelchecker.kinduction;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.kind.KInductionModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.*;
import static org.junit.Assert.assertEquals;

public class KInductionModelCheckerTest extends TestParser {

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

        result = new KInductionModelChecker(machine, 20).check();
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

        result = new KInductionModelChecker(machine, 20).check();
        assertEquals(COUNTER_EXAMPLE_FOUND, result.getType());
        assertEquals("{b=false, c=100000}", result.getLastState().toString());
        assertEquals(2, result.getSteps());
    }


    @Test
    public void testCounterCorrect() {
        machine = builder
            .setName("ebr")
            .setVariables("c")
            .setInvariant("c : INTEGER")
            .setInitialization(" c:= 0")
            .addOperation("inc = BEGIN c := c + 1 END")
            .build();

        result = new KInductionModelChecker(machine, 20).check();
        assertEquals(VERIFIED, result.getType());
    }
}
