package de.bmoth.performance;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@State(Scope.Benchmark)
public class ModelCheckerPerformanceTest {

    private String dir = "src/test/resources/machines/";

    @Test
    @Benchmark
    public void testLeuschelPerformanceMachines1() throws IOException {
        MachineNode machine = Parser.getMachineFileAsSemanticAst(dir + "/performance/CounterErr.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
    }

    @Test
    //@Benchmark
    public void testLeuschelPerformanceMachines2() throws IOException {
        MachineNode machine = Parser.getMachineFileAsSemanticAst(dir + "/performance/SimpleSetIncrease.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
    }

    @Test
    //@Benchmark
    public void testLeuschelPerformanceMachines3() throws IOException {
        MachineNode machine = Parser.getMachineFileAsSemanticAst(dir + "/performance/CounterErr2.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(false, result.isCorrect());
    }
}
