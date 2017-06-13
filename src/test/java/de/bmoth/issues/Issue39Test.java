package de.bmoth.issues;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;
public class Issue39Test {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testMachine1() throws IOException {
        MachineNode theMachine = parseMachineFromFile(dir + "SetVarToConstantViolation.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(theMachine);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testMachine2() throws IOException {
        MachineNode theMachine = parseMachineFromFile(dir + "SetVarToConstantNoViolation.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(theMachine);
        assertEquals(true, result.isCorrect());
    }
}
