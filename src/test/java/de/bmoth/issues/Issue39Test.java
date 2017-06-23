package de.bmoth.issues;

import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
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
        ModelCheckingResult result = ExplicitStateModelChecker.check(theMachine);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testMachine2() throws IOException {
        MachineNode theMachine = parseMachineFromFile(dir + "SetVarToConstantNoViolation.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(theMachine);
        assertEquals(true, result.isCorrect());
    }
}
