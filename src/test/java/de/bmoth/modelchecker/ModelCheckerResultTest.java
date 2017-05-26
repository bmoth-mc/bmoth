package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import de.bmoth.TestUsingZ3;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ModelCheckerResultTest extends TestUsingZ3 {
    State firstState;
    State secondState;
    State thirdState;

    String correct = "correct";
    String unknonw = "check-sat ...";
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
        ModelCheckingResult resultCorrect = new ModelCheckingResult(correct);
        ModelCheckingResult resultIncorrectUnknown = new ModelCheckingResult(unknonw);
        ModelCheckingResult resultIncorrectPath = new ModelCheckingResult(firstState);

        assertTrue(resultCorrect.isCorrect());
        assertFalse(resultIncorrectUnknown.isCorrect());
        assertFalse(resultIncorrectPath.isCorrect());
    }

    @Test
    public void testGetLastState() {
        ModelCheckingResult resultIncorrectPath = new ModelCheckingResult(firstState);
        assertEquals(firstState, resultIncorrectPath.getLastState());
    }

    @Test
    public void testGetPath() {
        assertEquals("[" + secondState + ", " + thirdState.toString() + "]", ModelCheckingResult.getPath(firstState).toString());
    }

    @Test
    public void testGetMessage() {
        ModelCheckingResult resultIncorrectUnknown = new ModelCheckingResult(unknonw);
        ModelCheckingResult resultIncorrectInvaild = new ModelCheckingResult(invalid);

        assertEquals(unknonw, resultIncorrectUnknown.getMessage());
        assertEquals("", resultIncorrectInvaild.getMessage());
    }
}
