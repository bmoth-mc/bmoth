package de.bmoth.modelchecker.esmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetSizesTest extends TestParser {

    private MachineBuilder builder;
    private MachineNode machine;
    private ModelCheckingResult result;

    @Before
    public void init() {
        builder = new MachineBuilder();
        machine = null;
    }

    @Test
    public void testXEqualEnumSet() {
        machine = builder
            .setName("XEqualEnumSet")
            .setSets("enm={d1,d2}")
            .setVariables("x")
            .setInvariant("x=enm & d1:x & d2:x")
            .setInitialization("x:=enm")
            .build();

        result = ExplicitStateModelChecker.check(machine);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testEnumeratedSetDoesNotChangeByInserting() {
        machine = builder
            .setName("EnumSetDoesNotChangeByInserting")
            .setSets("enm={d1,d2}")
            .setVariables("x")
            .setInvariant("x=enm")
            .setInitialization("x:=enm")
            .addOperation("add = ANY new WHERE new:enm THEN x := x \\/ {new} END")
            .build();

        result = ExplicitStateModelChecker.check(machine);
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getSteps());
    }

    @Test
    public void testXEqualDefSet() {
        machine = builder
            .setName("XEqualDefSet")
            .setSets("def")
            .setVariables("x")
            .setInvariant("x=def")
            .setInitialization("x:=def")
            .build();

        result = ExplicitStateModelChecker.check(machine);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testDeferredSetDoesNotChangeByInserting() {
        machine = builder
            .setName("DeferredSetDoesNotChangeByInserting")
            .setSets("def")
            .setVariables("x")
            .setInvariant("x=def")
            .setInitialization("x:=def")
            .addOperation("add = ANY new WHERE new:def THEN x := x \\/ {new} END")
            .build();

        result = ExplicitStateModelChecker.check(machine);
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getSteps());
    }
}
