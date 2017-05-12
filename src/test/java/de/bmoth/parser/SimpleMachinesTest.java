package de.bmoth.parser;

import org.junit.Test;

public class SimpleMachinesTest {

    @Test
    public void testMachine() throws Exception {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x : INTEGER & y : BOOL \n";
        machine += "INITIALISATION x := 1 || y := TRUE \n";
        machine += "OPERATIONS IncX = SELECT x < 10 THEN x := x + 1 END \n";
        machine += "END";

        Parser.getMachineAsSemanticAst(machine);
    }

}
