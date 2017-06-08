package de.bmoth.parser;

import org.junit.Test;

import de.bmoth.parser.cst.ScopeException;

public class SemanticChecksTest {

    @Test(expected = ScopeException.class)
    public void testUnknownIdentifier() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k2 = INTEGER \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }

    @Test(expected = ScopeException.class)
    public void testDuplicateClause() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }


    @Test
    public void testLocalIdentifier() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = {x | x : INTEGER } \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }


}
