package de.bmoth.modelchecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelCheckerTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testSimpleModelsWithoutOperations() throws Exception {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitViolation.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());

        MachineNode simpleMachineWithoutViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitNoViolation.mch");
        result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testSubstitution() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x:NATURAL & y : NATURAL \n";
        machine += "INITIALISATION x,y:= 1,2 \n";
        machine += "END";
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machine);
        ModelChecker.doModelCheck(machineAsSemanticAst);

        //TODO finish test
    }

    @Test
    public void testSimpleMachineWithOperations() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL & x >= 0 & x <= 2\n";
        machine += "INITIALISATION x := 0\n";
        machine += "OPERATIONS\n";
        machine += "\tInc = SELECT x < 2 THEN x := x + 1 END;\n";
        machine += "\tDec = SELECT x > 0 THEN x := x - 1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testSimpleMachineWithOperations2() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL & x >= 0 & x <= 2\n";
        machine += "INITIALISATION x := 0\n";
        machine += "OPERATIONS\n";
        machine += "\tBlockSubstitution = BEGIN x := x + 1 END\n";
        machine += "END";

        ModelCheckingResult result = ModelChecker.doModelCheck(machine);
        // the operation BlockSubstitution will finally violate the invariant x<=2
        assertEquals(false, result.isCorrect());
    }

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
