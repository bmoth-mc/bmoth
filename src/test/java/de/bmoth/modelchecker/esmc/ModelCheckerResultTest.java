package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.Expr;
import de.bmoth.TestUsingZ3;
import de.bmoth.modelchecker.State;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ModelCheckerResultTest extends TestUsingZ3 {
    State firstState;
    State secondState;
    State thirdState;

    String correct = "correct";
    String unknown = "check-sat ...";
    String invalid = "loremIpsum";

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
        ModelCheckingResult resultCorrect = new ModelCheckingResult(correct, 0);
        ModelCheckingResult resultIncorrectUnknown = new ModelCheckingResult(unknown, 0);
        ModelCheckingResult resultIncorrectPath = new ModelCheckingResult(firstState, 0);

        assertTrue(resultCorrect.isCorrect());
        assertFalse(resultIncorrectUnknown.isCorrect());
        assertFalse(resultIncorrectPath.isCorrect());
    }

    @Test
    public void testGetLastState() {
        ModelCheckingResult resultIncorrectPath = new ModelCheckingResult(firstState, 0);
        assertEquals(firstState, resultIncorrectPath.getLastState());
    }

    @Test
    public void testGetPath() {
        assertEquals("[" + secondState + ", " + thirdState.toString() + "]",
            firstState.getPath().toString());
    }

    @Test
    public void testGetMessage() {
        ModelCheckingResult resultIncorrectUnknown = new ModelCheckingResult(unknown, 0);
        ModelCheckingResult resultIncorrectInvaild = new ModelCheckingResult(invalid, 0);

        assertEquals(unknown, resultIncorrectUnknown.getMessage());
        assertEquals("", resultIncorrectInvaild.getMessage());
    }
}
