package de.bmoth.laws;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class BooleanLawsTest {
    private String dir = "src/test/resources/machines/laws/";

    @Test
    public void testSimpleModelsWithoutOperations() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "BoolLaws.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertTrue(result.isCorrect());
    }
}
