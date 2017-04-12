package de.bmoth.typechecker;

import java.util.Hashtable;
import java.util.List;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

public class TestTypechecker {

	public Hashtable<String, String> constants;
	public Hashtable<String, String> variables;

	public TestTypechecker(String machine) {
		MachineNode semanticAst = Parser.getMachineAsSemanticAst(machine);

		constants = new Hashtable<>();
		List<DeclarationNode> constantsDecls = semanticAst.getConstants();
		for (DeclarationNode declarationNode : constantsDecls) {
			constants.put(declarationNode.getName(), declarationNode.getType().toString());

		}

		variables = new Hashtable<>();
		List<DeclarationNode> variablesDecls = semanticAst.getVariables();
		for (DeclarationNode declarationNode : variablesDecls) {
			variables.put(declarationNode.getName(), declarationNode.getType().toString());

		}
	}

}