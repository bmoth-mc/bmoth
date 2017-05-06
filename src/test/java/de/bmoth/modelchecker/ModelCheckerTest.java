package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModelCheckerTest {
    @Test
    public void testSimpleModelsWithoutOperations() throws Exception {
        String dir = "src/test/resources/machines/";

        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitViolation.mch");
        boolean result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result);

        MachineNode simpleMachineWithoutViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitNoViolation.mch");
        result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(true, result);
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
    @Ignore
    public void testSimpleMachineWithOperations() throws Exception {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL & x >= 0 & x <= 2\n";
        machine += "INITIALISATION x := 0\n";
        machine += "OPERATIONS\n";
        machine += "\tInc = SELECT x < 2 THEN x := x + 1 END;\n";
        machine += "\tDec = SELECT x > 0 THEN x := x - 1 END\n";
        machine += "END";

        MachineNode simpleMachine = Parser.getMachineAsSemanticAst(machine);
        boolean result = ModelChecker.doModelCheck(simpleMachine);
        assertEquals(true, result);
    }
}
