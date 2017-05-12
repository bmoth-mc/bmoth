package de.bmoth.modelchecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelCheckerPerformanceTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testLeuschelPerformanceMachines1() throws Exception {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "/performance/CounterErr.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testLeuschelPerformanceMachines2() throws Exception {
        MachineNode simpleMachineWithoutViolation = Parser.getMachineFileAsSemanticAst(dir + "/performance/SimpleSetIncrease.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(false, result.isCorrect());
    }

}
