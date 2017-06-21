package de.bmoth.parser;

import static org.junit.Assert.fail;

import org.junit.Test;

import de.bmoth.TestParser;
import de.bmoth.parser.cst.ScopeException;

public class SemanticChecksTest {

    private static final String MACHINE_NAME = "MACHINE test\n";
    private static final String ONE_CONSTANT = "CONSTANTS k\n";


    @Test
    public void testUnknownIdentifier() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES k2 = INTEGER \n";
        machine += "END";
        parseMachineAndGetScopeException(machine);
    }

    @Test
    public void testDuplicateClause() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES k = INTEGER \n";
        machine += "PROPERTIES k = INTEGER \n";
        machine += "END";
        parseMachineAndGetScopeException(machine);
    }

    @Test
    public void testLocalIdentifier() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
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
