package de.bmoth.modelchecker.esmc;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.ABORTED;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelCheckerTest extends TestParser {

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
    public void testAnySubstitution() {
        machine = builder
            .setName("AnySubstitution")
            .setSets("")
            .setVariables("x, y")
            .setInvariant("x:NATURAL & y : NATURAL")
            .setInitialization("x,y:= 1,2")
            .addOperation("replaceBoth = ANY nVal WHERE nVal:1..3 THEN x,y := nVal,nVal END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(4, result.getSteps());
    }

    @Test
    public void testAnySubstitutionWithInvariantViolation() {
        machine = builder
            .setName("AnySubstitutionWithInvariantViolation")
            .setVariables("x")
            .setInvariant("x < 4")
            .setInitialization("x := 1")
            .addOperation("replaceX = ANY nVal WHERE nVal > 0 & nVal < 5 THEN x := nVal END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertFalse(result.isCorrect());
    }

    @Test
    public void testSimpleMachineWithOperations() {
        machine = builder
            .setName("SimpleMachineWithOperations")
            .setVariables("y")
            .setInvariant("y : NATURAL & y >= 0 & y <= 2")
            .setInitialization("y := 0")
            .addOperation("Inc = SELECT y < 2 THEN y := y + 1 END")
            .addOperation("Dec = SELECT y > 0 THEN y := y - 1 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
        assertEquals(3, result.getSteps());
    }

    @Test
    public void testSimpleMachineWithOperations2() {
        machine = builder
            .setName("SimpleMachineWithOperations2")
            .setVariables("z")
            .setInvariant("z : NATURAL & z >= 0 & z <= 2")
            .setInitialization("z := 0")
            .addOperation("BlockSubstitution = BEGIN z := z + 1 END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        // the operation BlockSubstitution will finally violate the invariant
        // x<=2
        assertFalse(result.isCorrect());
        assertEquals(4, result.getSteps());
    }

    @Test
    public void testAbort() {
        machine = builder
            .setName("Abort")
            .setVariables("x")
            .setInvariant("x : INTEGER")
            .setInitialization("x := 0")
            .addOperation("inc = BEGIN x := x + 1 END")
            .build();

        ExplicitStateModelChecker modelChecker = new ExplicitStateModelChecker(machine);

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            modelChecker.abort();
        }).start();

        result = modelChecker.check();

        assertFalse(result.isCorrect());
        assertEquals(ABORTED, result.getType());
    }

    @Test
    public void testEnumeratedSet() {
        machine = builder
            .setName("EnumeratedSet")
            .setSets("set={s1,s2,s3}")
            .setVariables("x")
            .setInvariant("x: set & x = s2")
            .setInitialization("x := s1")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        // the initialisation will finally violate the invariant x = s2
        assertFalse(result.isCorrect());
        assertEquals(1, result.getSteps());
    }

    @Test
    @Ignore("Z3 finds an empty model for this test?!")
    public void testDeferredSet() {
        machine = builder
            .setName("DeferredSet")
            .setSets("set")
            .setVariables("x,y")
            .setInvariant("x : set & y : set & x = y")
            .setInitialization("x :: set || y :: set")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        // the initialisation will finally violate the invariant x = y
        assertFalse(result.isCorrect());
        assertEquals(1, result.getSteps());
    }

    @Test
    public void testDeferredSetUsingAny() {
        machine = builder
            .setName("DeferredSetUsingAny")
            .setSets("set")
            .setVariables("x,y")
            .setInvariant("x : set & y : set & x = y")
            .setInitialization("ANY a,b WHERE a:set & b:set THEN x,y:=a,b END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        // the initialisation will finally violate the invariant x = y
        assertFalse(result.isCorrect());
    }

    @Test
    public void testIfThenElseSubstitution() {
        machine = builder
            .setName("IfThenElseSubstitution")
            .setVariables("a")
            .setInvariant("a : BOOL & a = TRUE")
            .setInitialization("IF 1=1 THEN a := TRUE ELSE a := FALSE END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
    }

    @Test
    public void testIfThenSubstitution() {
        machine = builder
            .setName("IfThenSubstitution")
            .setVariables("a, b")
            .setInvariant("a : BOOL & a = TRUE & b = TRUE")
            .setInitialization("a:= TRUE || b := TRUE")
            .addOperation("foo = IF 1=2 THEN a := FALSE|| b := FALSE END")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
    }

    @Test
    public void testBecomesElementOfSubstitution() {
        machine = builder
            .setName("BecomesElementOfSubstitution")
            .setVariables("a, b")
            .setInvariant("a = 3 & b : 4..5")
            .setInitialization("a :: {3} || b :: {4,5}")
            .build();

        result = new ExplicitStateModelChecker(machine).check();
        assertTrue(result.isCorrect());
    }
}
