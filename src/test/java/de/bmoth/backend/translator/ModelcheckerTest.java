package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;

public class ModelcheckerTest {

    
    @Test
    public void testSubstitution() throws Exception {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x:INTEGER & y : INTEGER \n";
        machine += "INITIALISATION x,y:= 1,2 \n";
        machine += "END";
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machine);
        ModelChecker.doModelCheck(machineAsSemanticAst);
        
        //TODO finish test
    }
}
