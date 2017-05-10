package de.bmoth.issues;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Issue39Test {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testMachine1() throws Exception {
        MachineNode theMachine = Parser.getMachineFileAsSemanticAst(dir + "SetVarToConstantViolation.mch");
        boolean result = ModelChecker.doModelCheck(theMachine);
        assertEquals(false, result);
    }

    @Test @Ignore
    public void testMachine2() throws Exception {
        MachineNode theMachine = Parser.getMachineFileAsSemanticAst(dir + "SetVarToConstantNoViolation.mch");
        boolean result = ModelChecker.doModelCheck(theMachine);
        assertEquals(true, result);
    }
}
