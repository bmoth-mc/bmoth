package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import de.bmoth.TestUsingZ3;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static de.bmoth.modelchecker.ModelCheckingResult.Type.*;
import static org.junit.Assert.*;

public class ModelCheckerResultTest extends TestUsingZ3 {
    State firstState;
    State secondState;
    State thirdState;
    State firstStateEquiv;

    String unknown = "check-sat ...";

    StateSpace stateSpace;

    @Before
    public void init() {
        HashMap<String, Expr> firstMap = new HashMap<>();
        HashMap<String, Expr> secondMap = new HashMap<>();
        HashMap<String, Expr> thirdMap = new HashMap<>();
        HashMap<String, Expr> firstMapEquiv = new HashMap<>();

        firstMap.put("x", z3Context.mkInt(10));
        secondMap.put("x", z3Context.mkInt(11));
        thirdMap.put("x", z3Context.mkInt(12));
        firstMapEquiv.put("x", z3Context.mkInt(10));

        thirdState = new State(thirdMap);
        secondState = new State(secondMap);
        firstState = new State(firstMap);
        firstStateEquiv = new State(firstMapEquiv);

        stateSpace = new StateSpace();

        // insert vertices
        stateSpace.addRootVertex(thirdState);
        stateSpace.addVertex(secondState);
        stateSpace.addVertex(firstState);

        // connect them
        stateSpace.addEdge(thirdState, secondState);
        stateSpace.addEdge(secondState, firstState);
    }

    @Test
    public void testIsCorrect() {
        //TODO think about state space root
        ModelCheckingResult resultCorrect = ModelCheckingResult.createVerified(0, null);
        ModelCheckingResult resultIncorrectUnknown = ModelCheckingResult.createUnknown(0, unknown);
        ModelCheckingResult resultIncorrectPath = ModelCheckingResult.createCounterExampleFound(0, firstState, stateSpace);

        assertTrue(resultCorrect.isCorrect());
        assertFalse(resultIncorrectUnknown.isCorrect());
        assertFalse(resultIncorrectPath.isCorrect());
    }

    @Test
    public void testGetLastState() {
        ModelCheckingResult resultIncorrectPath = ModelCheckingResult.createCounterExampleFound(0, firstState, stateSpace);
        assertEquals(firstState, resultIncorrectPath.getLastState());
    }

    @Test
    public void testGetStateSpace() {
        // TODO merge into one
        ModelCheckingResult resultNoStateSpace = ModelCheckingResult.createVerified(0, null);
        ModelCheckingResult resultWithStateSpace = ModelCheckingResult.createVerified(1, stateSpace);

        assertTrue(resultNoStateSpace.getStateSpace() == null);
        assertEquals("([{x=12}, {x=11}, {x=10}], [({x=12},{x=11}), ({x=11},{x=10})])", resultWithStateSpace.getStateSpace().toString());
    }

    @Test
    public void testGetPath() {
        ModelCheckingResult resultIncorrectPath = ModelCheckingResult.createCounterExampleFound(0, firstState, stateSpace);
        assertEquals("[{x=12}, {x=11}, {x=10}]", resultIncorrectPath.getCounterExamplePath().toString());
    }

    @Test
    public void testGetMessage() {
        ModelCheckingResult resultIncorrectUnknown = ModelCheckingResult.createUnknown(0, unknown);

        assertEquals(ModelCheckingResult.Type.UNKNOWN, resultIncorrectUnknown.getType());
        assertEquals(unknown, resultIncorrectUnknown.getReason());
    }

    @Test
    public void testType() {
        assertArrayEquals(new ModelCheckingResult.Type[]{COUNTER_EXAMPLE_FOUND, LTL_COUNTER_EXAMPLE_FOUND,
                EXCEEDED_MAX_STEPS, VERIFIED, ABORTED, UNKNOWN},
            ModelCheckingResult.Type.values());

        assertEquals(COUNTER_EXAMPLE_FOUND, ModelCheckingResult.Type.valueOf("COUNTER_EXAMPLE_FOUND"));
        assertEquals(EXCEEDED_MAX_STEPS, ModelCheckingResult.Type.valueOf("EXCEEDED_MAX_STEPS"));
    }

    @Test
    public void testToString() {
        assertEquals("UNKNOWN check-sat ... after 23 steps", ModelCheckingResult.createUnknown(23, unknown).toString());
        assertEquals("ABORTED after 15 steps", ModelCheckingResult.createAborted(15).toString());
        assertEquals("COUNTER_EXAMPLE_FOUND {x=11} after 12 steps", ModelCheckingResult.createCounterExampleFound(12, secondState, stateSpace).toString());
        assertEquals("EXCEEDED_MAX_STEPS after 17 steps", ModelCheckingResult.createExceededMaxSteps(17).toString());
        assertEquals("VERIFIED after 3 steps", ModelCheckingResult.createVerified(3, null).toString());
    }
}
