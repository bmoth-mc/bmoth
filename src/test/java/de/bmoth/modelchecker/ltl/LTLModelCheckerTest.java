package de.bmoth.modelchecker.ltl;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class LTLModelCheckerTest extends TestParser {

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
    public void testObviouslyBrokenProperty() {
        machine = builder
            .setName("CorrectCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G({0=1})\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertFalse(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    public void testObviouslyCorrectProperty() {
        machine = builder
            .setName("CorrectCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G {1=1}\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    public void testCorrectCounter() {
        machine = builder
            .setName("CorrectCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G {c<5}\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    @Ignore
    public void testCorrectCounterWithNext() {
        machine = builder
            .setName("CorrectCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G X {c<5}\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    public void testBrokenCounter() {
        machine = builder
            .setName("BrokenCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G {c<0}\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertFalse(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    public void testBrokenCounterWithNext() {
        machine = builder
            .setName("BrokenCounter")
            .setDefinitions("ASSERT_LTL_1 == \"G X {c<0}\"")
            .setSets("")
            .setVariables("c")
            .setInvariant("c:NAT")
            .setInitialization("c := 0")
            .addOperation("inc = PRE c < 2 THEN c:=c+1 END")
            .addOperation("reset = c:=0")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertFalse(result.isCorrect());
        assertEquals(3, result.getSteps());
    }
}
