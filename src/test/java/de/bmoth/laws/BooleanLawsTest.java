package de.bmoth.laws;

import de.bmoth.modelchecker.ExplicitStateModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static de.bmoth.TestParser.*;
public class BooleanLawsTest {
    private String dir = "src/test/resources/machines/laws/";

    @Test
    public void testSimpleModelsWithoutOperations() throws IOException {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "BoolLaws.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertTrue(result.isCorrect());
    }
}
