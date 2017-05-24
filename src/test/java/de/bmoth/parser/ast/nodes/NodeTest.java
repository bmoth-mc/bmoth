package de.bmoth.parser.ast.nodes;

import de.bmoth.parser.Parser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodeTest {
    MachineNode machine;

    @Before
    public void init() {
        machine = Parser.getMachineAsSemanticAst("MACHINE OpNodeMachine\nVARIABLES x\nINVARIANT x:INTEGER\nOPERATIONS\n\tset = BEGIN x := 1 END;\n\tselect = SELECT x = 1 THEN x := x END\nEND");
    }

    @Test
    public void testOperationNode() {
        OperationNode setOperation = machine.getOperations().get(0);
        OperationNode selectOperation = machine.getOperations().get(1);
        assertEquals("set", setOperation.getName());
        assertEquals("set = BEGIN x := 1 END", setOperation.toString());
        assertEquals("select = SELECT EQUAL(x,1) THEN x := x END", selectOperation.toString());
        assertEquals("[x]", setOperation.getAssignedDeclarationNodes().toString());
    }
}
