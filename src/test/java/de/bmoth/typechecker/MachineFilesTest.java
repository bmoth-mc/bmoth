package de.bmoth.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MachineFilesTest {

    @Test
    public void testReadSimpleMachineFromFile() throws Exception {
        String dir = "src/test/resources/machines/";
        MachineNode machineNode = Parser.getMachineFileAsSemanticAst(dir + "SimpleMachine.mch");
        List<DeclarationNode> variables = machineNode.getVariables();
        assertEquals(1, variables.size());
        assertEquals("x", variables.get(0).getName());
        assertEquals("INTEGER", variables.get(0).getType().toString());
    }
}
