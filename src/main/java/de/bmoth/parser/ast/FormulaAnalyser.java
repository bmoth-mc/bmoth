package de.bmoth.parser.ast;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
		private final LinkedList<LinkedHashMap<String, Token>> scopeTable = new LinkedList<>();
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
		
		
		@Override
		public Void visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
			List<Token> identifiers = ctx.identifier_list().identifiers;
			LinkedHashMap<String, Token> localIdentifiers = new LinkedHashMap<>();
			for (Token token : identifiers) {
				localIdentifiers.put(token.getText(), token);
			}
			scopeTable.add(localIdentifiers);
			ctx.predicate().accept(this);
			scopeTable.removeLast();
			return null;
		}
		

		private void visitIdentifierToken(Token identifierToken) {
			String name = identifierToken.getText();
			for (int i = scopeTable.size() - 1; i >= 0; i--) {
				LinkedHashMap<String, Token> map = scopeTable.get(i);
				if (map.containsKey(name)) {
					Token declarationToken = map.get(name);
					declarationReferences.put(identifierToken, declarationToken);
					return;
				}
			}
			if (implicitDeclarations.containsKey(name)) {
				declarationReferences.put(identifierToken, implicitDeclarations.get(name));
			} else {
				implicitDeclarations.put(name, identifierToken);
				declarationReferences.put(identifierToken, identifierToken);
			}
		}
	}

}
