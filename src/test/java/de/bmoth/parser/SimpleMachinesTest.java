package de.bmoth.parser;

import org.antlr.v4.runtime.tree.ParseTree;

//import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.bmoth.parser.ast.nodes.MachineNode;

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

		parse(machine);
	}

	private void parse(String machine) {
		Parser parser = new Parser();
		ParseTree parseTree = parser.parseString(machine);
		MachineNode ast = parser.getAst(parseTree);
		System.out.println(ast);
	}

}
