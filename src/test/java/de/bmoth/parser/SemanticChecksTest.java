package de.bmoth.parser;

import org.junit.Test;

import de.bmoth.exceptions.ScopeException;

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


}
