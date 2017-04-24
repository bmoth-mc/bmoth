package de.bmoth.parser.ast;

import java.util.LinkedHashMap;

import org.antlr.v4.runtime.Token;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.antlr.BMoThParser.FormulaContext;

public class FormulaAnalyser {
	final FormulaContext formula;
	final LinkedHashMap<String, Token> implicitDeclarations = new LinkedHashMap<>();
	final LinkedHashMap<Token, Token> declarationReferences = new LinkedHashMap<>();

	public FormulaAnalyser(FormulaContext formula) {
		this.formula = formula;
		VariablesFinder variablesFinder = new VariablesFinder();
		formula.accept(variablesFinder);
	}

	class VariablesFinder extends BMoThParserBaseVisitor<Void> {
		/*
		 * Note, we have to add a scope table when quantified variables are
		 * introduced.
		 */

		@Override
		public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
			Token identifierToken = ctx.IDENTIFIER().getSymbol();
			visitIdentifierToken(identifierToken);
			return null;
		}

		@Override
		public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
			Token identifierToken = ctx.IDENTIFIER().getSymbol();
			visitIdentifierToken(identifierToken);
			return null;
		}

		private void visitIdentifierToken(Token identifierToken) {
			String name = identifierToken.getText();
			if (implicitDeclarations.containsKey(name)) {
				declarationReferences.put(identifierToken, implicitDeclarations.get(name));
			} else {
				implicitDeclarations.put(name, identifierToken);
				declarationReferences.put(identifierToken, identifierToken);
			}
		}
	}

}
