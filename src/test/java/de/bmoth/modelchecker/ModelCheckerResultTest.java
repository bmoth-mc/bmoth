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

    String unknown = "check-sat ...";

    @Before
    public void init() {
        HashMap<String, Expr> firstMap = new HashMap<>();
        HashMap<String, Expr> secondMap = new HashMap<>();
        HashMap<String, Expr> thirdMap = new HashMap<>();

        firstMap.put("x", z3Context.mkInt(10));
        secondMap.put("x", z3Context.mkInt(11));
        thirdMap.put("x", z3Context.mkInt(12));

        thirdState = new State(null, thirdMap);
        secondState = new State(thirdState, secondMap);
        firstState = new State(secondState, firstMap);
    }

    @Test
    public void testIsCorrect() {
        ModelCheckingResult resultCorrect = ModelCheckingResult.createVerified(0);
        ModelCheckingResult resultIncorrectUnknown = ModelCheckingResult.createUnknown(0, unknown);
        ModelCheckingResult resultIncorrectPath = ModelCheckingResult.createCounterExampleFound(0, firstState);

        assertTrue(resultCorrect.isCorrect());
        assertFalse(resultIncorrectUnknown.isCorrect());
        assertFalse(resultIncorrectPath.isCorrect());
    }

    @Test
    public void testGetLastState() {
        ModelCheckingResult resultIncorrectPath = ModelCheckingResult.createCounterExampleFound(0, firstState);
        assertEquals(firstState, resultIncorrectPath.getLastState());
    }

    @Test
    public void testGetPath() {
        assertEquals("[" + secondState + ", " + thirdState.toString() + "]",
            firstState.getPath().toString());
    }

    @Test
    public void testGetMessage() {
        ModelCheckingResult resultIncorrectUnknown = ModelCheckingResult.createUnknown(0, unknown);

        assertEquals(ModelCheckingResult.Type.UNKNOWN, resultIncorrectUnknown.getType());
        assertEquals(unknown, resultIncorrectUnknown.getReason());
    }

    @Test
    public void testType() {
        assertArrayEquals(new ModelCheckingResult.Type[]{COUNTER_EXAMPLE_FOUND,
                EXCEEDED_MAX_STEPS, VERIFIED, ABORTED, UNKNOWN},
            ModelCheckingResult.Type.values());

        assertEquals(COUNTER_EXAMPLE_FOUND, ModelCheckingResult.Type.valueOf("COUNTER_EXAMPLE_FOUND"));
        assertEquals(EXCEEDED_MAX_STEPS, ModelCheckingResult.Type.valueOf("EXCEEDED_MAX_STEPS"));
    }
}
