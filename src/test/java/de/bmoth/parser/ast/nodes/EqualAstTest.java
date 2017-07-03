package de.bmoth.parser.ast.nodes;

import de.bmoth.TestParser;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;


public class EqualAstTest extends TestParser {
    @Test
    public void testEqualAstIfSubstitution() {
        MachineNode machine = new MachineBuilder()
            .setName("IfSubstitutionMachine")
            .setVariables("a")
            .setInvariant("a : INTEGER")
            .setInitialization("a := 0")
            .addOperation("op1 = IF a = 3 THEN a:=2 ELSE a:=1 END")
            .addOperation("op2 = IF a = 3 THEN a:=2 ELSE a:=1 END")
            .addOperation("op_changed1 = IF a = 2 THEN a:=2 ELSE a:=1 END")
            .addOperation("op_changed2 = IF a = 3 THEN a:=5 ELSE a:=1 END")
            .addOperation("op_changed3 = IF a = 3 THEN a:=2 ELSE a:=3 END")
            .build();

        List<SubstitutionNode> substitutions = machine.getOperations().stream().map(OperationNode::getSubstitution).collect(Collectors.toList());
        assertTrue(substitutions.get(0).equalAst(substitutions.get(1)));
        assertFalse(substitutions.get(0).equalAst(substitutions.get(2)));
        assertFalse(substitutions.get(0).equalAst(substitutions.get(3)));
        assertFalse(substitutions.get(0).equalAst(substitutions.get(4)));
    }
}

