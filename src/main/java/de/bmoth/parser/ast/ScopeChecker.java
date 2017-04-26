package de.bmoth.parser.ast;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.antlr.BMoThParser.PredicateContext;

public class ScopeChecker extends BMoThParserBaseVisitor<Void> {
	final LinkedList<LinkedHashMap<String, Token>> scopeTable = new LinkedList<>();
	final AbstractAnalyser analyser;

	ScopeChecker(AbstractAnalyser analyser) {
		this.analyser = analyser;
	}

	@Override
	public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
		Token identifierToken = ctx.IDENTIFIER().getSymbol();
		lookUpToken(identifierToken);
		return null;
	}

	@Override
	public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
		Token identifierToken = ctx.IDENTIFIER().getSymbol();
		lookUpToken(identifierToken);
		return null;
	}

	@Override
	public Void visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
		List<Token> identifiers = ctx.identifier_list().identifiers;
		PredicateContext predicate = ctx.predicate();
		visitQuantifiedFormula(identifiers, predicate);
		return null;
	}

	@Override
	public Void visitQuantifiedPredicate(BMoThParser.QuantifiedPredicateContext ctx) {
		List<Token> identifiers = ctx.quantified_variables_list().identifier_list().identifiers;
		PredicateContext predicate = ctx.predicate();
		visitQuantifiedFormula(identifiers, predicate);
		return null;
	}

	private void visitQuantifiedFormula(List<Token> identifiers, PredicateContext predicate) {
		LinkedHashMap<String, Token> localIdentifiers = new LinkedHashMap<>();
		for (Token token : identifiers) {
			localIdentifiers.put(token.getText(), token);
		}
		scopeTable.add(localIdentifiers);
		predicate.accept(this);
		scopeTable.removeLast();
	}

	@Override
	public Void visitAssignSubstitution(BMoThParser.AssignSubstitutionContext ctx) {
		List<Token> identifiers = ctx.identifier_list().identifiers;
		for (Token token : identifiers) {
			lookUpToken(token);
			// String name = token.getText();
			// if (MachineAnalyser.this.variablesDeclarations.containsKey(name))
			// {
			// Token variableDeclaration =
			// MachineAnalyser.this.variablesDeclarations.get(name);
			// declarationReferences.put(token, variableDeclaration);
			// } else {
			// throw new ScopeException(token, "Identifier '" + name + "' must
			// be a variable.");
			// }
		}
		ctx.expression_list().accept(this);
		return null;
	}

	private void lookUpToken(Token identifierToken) {
		String name = identifierToken.getText();
		for (int i = scopeTable.size() - 1; i >= 0; i--) {
			LinkedHashMap<String, Token> map = scopeTable.get(i);
			if (map.containsKey(name)) {
				Token declarationToken = map.get(name);
				analyser.addDeclarationReference(identifierToken, declarationToken);
				return;
			}
		}
		analyser.identifierNodeFound(identifierToken);
	}

}
