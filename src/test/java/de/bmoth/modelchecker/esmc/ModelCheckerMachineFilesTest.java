package de.bmoth.modelchecker.esmc;

import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class ModelCheckerMachineFilesTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testSimpleModelsWithoutOperations() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "OnlyInitViolation.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
        assertEquals(1, result.getSteps());

        MachineNode simpleMachineWithoutViolation = parseMachineFromFile(dir + "OnlyInitNoViolation.mch");
        result = ExplicitStateModelChecker.check(simpleMachineWithoutViolation);
        assertEquals(true, result.isCorrect());
        assertEquals(1, result.getSteps());
    }

    @Test
    public void testSimpleModelWithoutVariables() {
        MachineNode simpleMachineWithViolation = parseMachineFromFile(dir + "OnlyConstants.mch");
        ModelCheckingResult result = ExplicitStateModelChecker.check(simpleMachineWithViolation);
        assertEquals(true, result.isCorrect());
    }
}
