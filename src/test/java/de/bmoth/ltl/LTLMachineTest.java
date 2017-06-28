package de.bmoth.ltl;

import static de.bmoth.TestConstants.MACHINE_NAME;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;

public class LTLMachineTest {

    @Test
    public void testSimpleLTLTestMachine() {
        String machine = MACHINE_NAME;
        machine += "DEFINITIONS ASSERT_LTL_1 == \"F({x=7})\"  ";
        machine += "VARIABLES x \n";
        machine += "INVARIANT x : 1..10 \n";
        machine += "INITIALISATION x := 1\n";
        machine += "OPERATIONS foo = SELECT x < 5 THEN x := x + 1 END\n";
        machine += "END";
        MachineNode machineAST = getMachineAST(machine);
        ArrayList<LTLFormula> ltlFormulas = machineAST.getLTLFormulas();
        LTLFormula ltl1 = ltlFormulas.get(0);
        assertEquals("ASSERT_LTL_1", ltl1.getName());
        System.out.println(ltl1.getLTLNode());
    }

    private MachineNode getMachineAST(String machine) {
        try {
            return Parser.getMachineAsSemanticAst(machine);
        } catch (ParserException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return null;
        }
    }
}
