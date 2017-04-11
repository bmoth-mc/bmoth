package de.bmoth.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.antlr.BMoThLexer;
import de.bmoth.antlr.BMoThParser;
import de.bmoth.parser.ast.MachineAnalyser;
import de.bmoth.parser.ast.SemanticAstCreator;
import de.bmoth.parser.ast.nodes.MachineNode;

public class Parser {

	public ParseTree parseString(String inputString) {
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		final BMoThLexer lexer = new BMoThLexer(inputStream);
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BMoThParser bMoThParser = new BMoThParser(tokens);

		bMoThParser.addErrorListener(new DiagnosticErrorListener());
		MyErrorListener myErrorListener = new MyErrorListener();
		bMoThParser.addErrorListener(myErrorListener);
		ParseTree tree = bMoThParser.start();
		return tree;
	}

	public MachineNode getAst(ParseTree parseTree) {
		MachineAnalyser machineAnalyser = new MachineAnalyser(parseTree);
		SemanticAstCreator astCreator = new SemanticAstCreator(machineAnalyser);
		return astCreator.getMachineNode();
	}

	public Parser() {

	}

}
