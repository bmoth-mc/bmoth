package de.bmoth.issues;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Issue66Test {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testMachine() throws IOException {
        MachineNode theMachine = Parser.getMachineFileAsSemanticAst(dir + "LargeExponent.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(theMachine);
        assertEquals(false, result.isCorrect());
    }
}
