package de.bmoth.parser;

import de.bmoth.exceptions.ScopeException;
import org.junit.Test;

public class SemanticChecksTest {

    @Test(expected = ScopeException.class)
    public void testUnknownIdentifier() throws Exception {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k2 = INTEGER \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }

    @Test(expected = ScopeException.class)
    public void testDuplicateClause() throws Exception {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }


    @Test
    public void testLocalIdentifier() throws Exception {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = {x | x : INTEGER } \n";
        machine += "END";
        Parser.getMachineAsSemanticAst(machine);
    }


}
