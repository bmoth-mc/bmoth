package de.bmoth.parser.ast.nodes;

import de.bmoth.parser.Parser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodeTest {
    MachineNode machine;

    @Before
    public void init() {
        machine = Parser.getMachineAsSemanticAst("MACHINE OpNodeMachine\nVARIABLES x\nINVARIANT x:INTEGER\nOPERATIONS set = BEGIN x := 1 END\nEND");
    }

    @Test
    public void testOperationNode() {
        OperationNode opnode = machine.getOperations().get(0);
        assertEquals("set", opnode.getName());
        assertEquals("set = BEGIN x := 1 END", opnode.toString());
        assertEquals("[x]", opnode.getAssignedDeclarationNodes().toString());
    }
}
