package de.bmoth.modelchecker;

import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.esmc.ModelCheckingResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;
public class SetSizesTest {
    @Test
    public void testXEqualEnumSet() {
        String machine = "MACHINE test\n";
        machine += "SETS enm={d1,d2}\n";
        machine += "VARIABLES\n";
        machine += "x\n";
        machine += "INVARIANT x=enm & d1:x & d2:x\n";
        machine += "INITIALISATION x:=enm\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testEnumeratedSetDoesNotChangeByInserting() {
        String machine = "MACHINE test\n";
        machine += "SETS enm={d1,d2}\n";
        machine += "VARIABLES\n";
        machine += "x\n";
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
        String machine = "MACHINE test\n";
        machine += "SETS def\n";
        machine += "VARIABLES\n";
        machine += "x\n";
        machine += "INVARIANT x=def\n";
        machine += "INITIALISATION x:=def\n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testDefferedSetDoesNotChangeByInserting() {
        String machine = "MACHINE test\n";
        machine += "SETS def\n";
        machine += "VARIABLES\n";
        machine += "x\n";
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
