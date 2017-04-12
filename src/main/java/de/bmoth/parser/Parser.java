package de.bmoth.parser;

import org.antlr.v4.runtime.*;

import de.bmoth.antlr.BMoThLexer;
import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.FormulaContext;
import de.bmoth.antlr.BMoThParser.StartContext;
import de.bmoth.parser.ast.FormulaAnalyser;
import de.bmoth.parser.ast.MachineAnalyser;
import de.bmoth.parser.ast.SemanticAstCreator;
import de.bmoth.parser.ast.TypeChecker;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

public class Parser {

	private BMoThParser getParser(String inputString) {
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		final BMoThLexer lexer = new BMoThLexer(inputStream);
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		BMoThParser bMoThParser = new BMoThParser(tokens);
		bMoThParser.addErrorListener(new DiagnosticErrorListener());
		MyErrorListener myErrorListener = new MyErrorListener();
		bMoThParser.addErrorListener(myErrorListener);
		return bMoThParser;
	}

	public StartContext parseMachine(String inputString) {
		BMoThParser parser = getParser(inputString);
		StartContext start = parser.start();
		return start;
	}

	public FormulaContext parseFormula(String inputString) {
		BMoThParser parser = getParser(inputString);
		return parser.formula();
	}

	public MachineNode getMachineAst(StartContext start) {
		MachineAnalyser machineAnalyser = new MachineAnalyser(start);
		SemanticAstCreator astCreator = new SemanticAstCreator(machineAnalyser);
		return (MachineNode) astCreator.getAstNode();
	}

	public FormulaNode getFormulaAst(FormulaContext formula) {
		FormulaAnalyser formulaAnalyser = new FormulaAnalyser(formula);
		SemanticAstCreator astCreator = new SemanticAstCreator(formulaAnalyser);
		return (FormulaNode) astCreator.getAstNode();
	}

	public static MachineNode getMachineAsSemanticAst(String inputString) {
		Parser parser = new Parser();
		StartContext start = parser.parseMachine(inputString);
		MachineNode ast = parser.getMachineAst(start);
		new TypeChecker(ast);
		return ast;
	}

	public static FormulaNode getFormulaAsSemanticAst(String inputString) {
		Parser parser = new Parser();
		FormulaContext formulaContext = parser.parseFormula(inputString);
		FormulaNode formulaNode = parser.getFormulaAst(formulaContext);
		new TypeChecker(formulaNode);
		return formulaNode;
	}

}
