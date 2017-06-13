package de.bmoth.modelchecker;

import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class ModelCheckerMachineFilesTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testSimpleModelsWithoutOperations() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "OnlyInitViolation.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());

        MachineNode simpleMachineWithoutViolation = parseMachineFromFile(dir + "OnlyInitNoViolation.mch");
        result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getNumberOfDistinctStatesVisited());
    }

    @Test
    public void testSimpleModelWithoutVariables() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "OnlyConstants.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(true, result.isCorrect());
    }
}
