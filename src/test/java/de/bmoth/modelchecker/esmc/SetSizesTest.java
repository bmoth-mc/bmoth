package de.bmoth.modelchecker.esmc;

import org.junit.Test;

import static de.bmoth.TestParser.parseMachine;
import static org.junit.Assert.assertEquals;

public class SetSizesTest {

    private static final String MACHINE_NAME = "MACHINE test\n";
    private static final String ONE_VARIABLE_X = "VARIABLES\nx\n";

    @Test
    public void testXEqualEnumSet() {
        String machine = MACHINE_NAME;
        machine += "SETS enm={d1,d2}\n";
        machine += ONE_VARIABLE_X;
        machine += "INVARIANT x=enm & d1:x & d2:x\n";
        machine += "INITIALISATION x:=enm\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testEnumeratedSetDoesNotChangeByInserting() {
        String machine = MACHINE_NAME;
        machine += "SETS enm={d1,d2}\n";
        machine += ONE_VARIABLE_X;
        machine += "INVARIANT x=enm\n";
        machine += "INITIALISATION x:=enm\n";
        machine += "OPERATIONS\n";
        machine += "add = ANY new WHERE new:enm THEN x := x \\/ {new} END\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());
    }

    @Test
    public void testXEqualDefSet() {
        String machine = MACHINE_NAME;
        machine += "SETS def\n";
        machine += ONE_VARIABLE_X;
        machine += "INVARIANT x=def\n";
        machine += "INITIALISATION x:=def\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testDefferedSetDoesNotChangeByInserting() {
        String machine = MACHINE_NAME;
        machine += "SETS def\n";
        machine += ONE_VARIABLE_X;
        machine += "INVARIANT x=def\n";
        machine += "INITIALISATION x:=def\n";
        machine += "OPERATIONS\n";
        machine += "add = ANY new WHERE new:def THEN x := x \\/ {new} END\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());
    }
}
