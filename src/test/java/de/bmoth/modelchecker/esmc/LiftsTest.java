package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.Expr;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static de.bmoth.TestParser.*;

public class LiftsTest {

    private String dir = "src/test/resources/machines/lifts/";

    @Test
    public void testCrashing() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "CrashingLift.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{old_floor=3, old_time=3, current_time=4, current_floor=0}", result.getLastState().toString());
    }

    @Test
    public void testLowerHigher() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "LiftLowerHigher.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{doors_open=false, moving=true, current_floor=-1}", result.getLastState().toString());
    }

    @Test
    public void testInvalidPositions() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "LiftStopsAtInvalidPositions.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{MAX_FLOOR=5, MIN_FLOOR=0, doors_open=false, moving=false, current_floor=1, betweenFloors=true}",
                result.getLastState().toString());
    }

    @Ignore("iseq not implemented in FormularToZ3Translator")
    @Test
    public void testLiftDoesNotMoveTowardsFirstPressedButton() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(
                dir + "LiftDoesNotMoveTowardsFirstPressedButton.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testOpenDoorWhileMoving() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "OpenDoorWhileMoving.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{doors_open=true, moving=true}", result.getLastState().toString());
    }

    @Test
    public void testTargetAndCurrentCorrespond() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "TargetAndCurrentCorrespond.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        Expr targetFloor = result.getLastState().getValues().get("target_floor");
        Expr currentFloor = result.getLastState().getValues().get("current_floor");
        assertNotEquals(targetFloor.toString(), currentFloor.toString());
    }

    @Test
    public void testMissingEmergencyCall() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "EmergencyCallFail.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testNotMoving() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "NotMoving.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testAcceleration() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "AccMachine.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testSlowDoors() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "SlowDoors.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testFastDoors() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "FastDoors.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }
}
