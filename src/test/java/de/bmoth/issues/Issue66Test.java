package de.bmoth.issues;

import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.esmc.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class Issue66Test {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testMachine() throws IOException {
        MachineNode theMachine = parseMachineFromFile(dir + "LargeExponent.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(theMachine);
        assertEquals(false, result.isCorrect());
    }
}
