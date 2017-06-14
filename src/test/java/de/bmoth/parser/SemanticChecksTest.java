package de.bmoth.parser;

import static org.junit.Assert.fail;

import org.junit.Test;

import de.bmoth.TestParser;
import de.bmoth.parser.cst.ScopeException;

public class SemanticChecksTest {

    @Test
    public void testUnknownIdentifier() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k2 = INTEGER \n";
        machine += "END";
        parseMachineAndGetScopeException(machine);
    }

    @Test
    public void testDuplicateClause() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "END";
        parseMachineAndGetScopeException(machine);
    }

    @Test
    public void testLocalIdentifier() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = {x | x : INTEGER } \n";
        machine += "END";
        TestParser.parseMachine(machine);
    }

    private String parseMachineAndGetScopeException(String machine) {
        try {
            Parser.getMachineAsSemanticAst(machine);
            fail("Expected scope exception.");
        } catch (ParserException e) {
            if (e.getException() instanceof ScopeException) {
                return e.getMessage();
            } else {
                fail("Expected scope exception.");
            }
        }
        return null;
    }

}
