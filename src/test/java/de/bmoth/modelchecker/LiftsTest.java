package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LiftsTest {

    private String dir = "src/test/resources/machines/lifts/";

    @Test
    public void testCrashing() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "CrashingLift.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{old_floor=3, old_time=3, current_time=4, current_floor=0}", result.getLastState().toString());
    }

    @Test
    public void testLowerHigher() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "LiftLowerHigher.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{doors_open=false, moving=true, current_floor=-1}", result.getLastState().toString());
    }

    @Test
    public void testInvalidPositions() throws IOException {
        MachineNode simpleMachineWithViolation = Parser
            .getMachineFileAsSemanticAst(dir + "LiftStopsAtInvalidPositions.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{MAX_FLOOR=5, MIN_FLOOR=0, doors_open=false, moving=false, current_floor=1, betweenFloors=true}",
            result.getLastState().toString());
    }

    @Ignore("Error in Type Checker")
    @Test
    public void testLiftDoesNotMoveTowardsFirstPressedButton() throws IOException {
        MachineNode simpleMachineWithViolation = Parser
            .getMachineFileAsSemanticAst(dir + "LiftDoesNotMoveTowardsFirstPressedButton.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testOpenDoorWhileMoving() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OpenDoorWhileMoving.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals("{doors_open=true, moving=true}", result.getLastState().toString());
    }

    @Test
    public void testTargetAndCurrentCorrespond() throws IOException {
        MachineNode simpleMachineWithViolation = Parser
            .getMachineFileAsSemanticAst(dir + "TargetAndCurrentCorrespond.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        Expr targetFloor = result.getLastState().values.get("target_floor");
        Expr currentFloor = result.getLastState().values.get("current_floor");
        assertNotEquals(targetFloor.toString(), currentFloor.toString());
    }

    @Test
    public void testMissingEmergencyCall() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "EmergencyCallFail.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testNotMoving() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "NotMoving.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testAcceleration() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "AccMachine.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testSlowDoors() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "SlowDoors.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testFastDoors() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "FastDoors.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }
}
