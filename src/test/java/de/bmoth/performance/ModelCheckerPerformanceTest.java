package de.bmoth.performance;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelCheckerPerformanceTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testLeuschelPerformanceMachines1() throws Exception {
        MachineNode machine = Parser.getMachineFileAsSemanticAst(dir + "/performance/CounterErr.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
    }

    @Test
    public void testLeuschelPerformanceMachines2() throws Exception {
        MachineNode machine = Parser.getMachineFileAsSemanticAst(dir + "/performance/SimpleSetIncrease.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
    }

}
