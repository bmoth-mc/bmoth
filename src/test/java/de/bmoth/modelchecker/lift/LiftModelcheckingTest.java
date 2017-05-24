package de.bmoth.modelchecker.lift;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;

public class LiftModelcheckingTest {

    private String dir = "src/test/resources/machines/";

    @Test
    public void testSimpleModelsWithoutOperations() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "LiftStopsAtInvalidPositions.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }
}
