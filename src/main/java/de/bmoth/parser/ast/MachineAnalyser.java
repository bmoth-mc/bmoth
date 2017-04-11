package de.bmoth.parser.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import static de.bmoth.antlr.BMoThParser.*;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.Identifier_listContext;
import de.bmoth.antlr.BMoThParser.OperationContext;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.exceptions.ScopeException;

public class MachineAnalyser {

	private final ParseTree parseTree;

	final LinkedHashMap<String, Token> constantsDeclarations = new LinkedHashMap<>();
	final LinkedHashMap<String, Token> variablesDeclarations = new LinkedHashMap<>();
	final LinkedHashMap<String, OperationContext> operationsDeclarations = new LinkedHashMap<>();

	final LinkedHashMap<Token, Token> declarationReferences = new LinkedHashMap<>();

	PredicateClauseContext properties;
	PredicateClauseContext invariant;
	InitialisationClauseContext initialisation;

	public MachineAnalyser(ParseTree parseTree) {
		this.parseTree = parseTree;

		// find and store all declaration of global identifiers
		new DeclarationFinder();

		// check that all used identifiers are declared
		// store a reference for each to identifier to its declaration
		new ScopeChecker();
	}

	class DeclarationFinder extends BMoThParserBaseVisitor<Void> {
		DeclarationFinder() {
			parseTree.accept(this);
		}

		@Override
		public Void visitDeclarationClause(DeclarationClauseContext ctx) {
			List<Token> identifiers = ctx.identifier_list().identifiers;
			LinkedHashMap<String, Token> declarations = new LinkedHashMap<>();
			for (Token token : identifiers) {
				checkGlobalIdentifiers(token);
				declarations.put(token.getText(), token);
			}
			switch (ctx.clauseName.getType()) {
			case CONSTANTS:
				constantsDeclarations.putAll(declarations);
				break;
			case VARIABLES:
				variablesDeclarations.putAll(declarations);
				break;
			default:
				unreachable();
			}
			return null;
		}

		private void checkGlobalIdentifiers(Token token) {
			String name = token.getText();
			if (MachineAnalyser.this.constantsDeclarations.containsKey(name)
					|| MachineAnalyser.this.variablesDeclarations.containsKey(name)
					|| MachineAnalyser.this.operationsDeclarations.containsKey(name)) {
				throw new ScopeException(token, "Duplicate declaration of identifier: ");
			}
		}

		private void unreachable() {
			throw new AssertionError("Should not be reachable due to the antlr grammar.");
		}

		@Override
		public Void visitOperation(BMoThParser.OperationContext ctx) {
			Token nameToken = ctx.IDENTIFIER().getSymbol();
			checkGlobalIdentifiers(nameToken);
			String name = nameToken.getText();
			operationsDeclarations.put(name, ctx);
			return null;
		}

		@Override
		public Void visitPredicateClause(BMoThParser.PredicateClauseContext ctx) {
			switch (ctx.clauseName.getType()) {
			case INVARIANT:
				if (MachineAnalyser.this.invariant == null) {
					MachineAnalyser.this.invariant = ctx;
				} else {
					throw new ScopeException(ctx, "Duplicate INVARIANT clause.");
				}
				break;
			case PROPERTIES:
				if (MachineAnalyser.this.properties == null) {
					MachineAnalyser.this.properties = ctx;
				} else {
					throw new ScopeException(ctx, "Duplicate PROPERTIES clause.");
				}
				break;
			default:
				unreachable();
			}
			return null;
		}

		@Override
		public Void visitInitialisationClause(BMoThParser.InitialisationClauseContext ctx) {
			if (MachineAnalyser.this.initialisation == null) {
				MachineAnalyser.this.initialisation = ctx;
			} else {
				throw new ScopeException(ctx, "Duplicate PROPERTIES clause.");
			}
			return null;
		}

	}

	class ScopeChecker extends BMoThParserBaseVisitor<Void> {
		private final List<LinkedHashMap<String, Token>> scopeTable = new ArrayList<>();

		ScopeChecker() {
			if (MachineAnalyser.this.properties != null) {
				scopeTable.clear();
				scopeTable.add(MachineAnalyser.this.constantsDeclarations);
				MachineAnalyser.this.properties.accept(this);
				scopeTable.clear();
			}

			if (MachineAnalyser.this.invariant != null) {
				scopeTable.clear();
				scopeTable.add(MachineAnalyser.this.constantsDeclarations);
				scopeTable.add(MachineAnalyser.this.variablesDeclarations);
				MachineAnalyser.this.invariant.accept(this);
				scopeTable.clear();
			}

			if (MachineAnalyser.this.initialisation != null) {
				scopeTable.clear();
				scopeTable.add(MachineAnalyser.this.constantsDeclarations);
				scopeTable.add(MachineAnalyser.this.variablesDeclarations);
				MachineAnalyser.this.initialisation.accept(this);
				scopeTable.clear();
			}

			for (Entry<String, OperationContext> entry : MachineAnalyser.this.operationsDeclarations.entrySet()) {
				scopeTable.clear();
				scopeTable.add(MachineAnalyser.this.constantsDeclarations);
				scopeTable.add(MachineAnalyser.this.variablesDeclarations);
				entry.getValue().substitution().accept(this);
				scopeTable.clear();
			}

		}

		@Override
		public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
			Token identifierToken = ctx.IDENTIFIER().getSymbol();
			lookUpToken(identifierToken);
			return null;
		}

		@Override
		public Void visitAssignSubstitution(BMoThParser.AssignSubstitutionContext ctx) {
			List<Token> identifiers = ctx.identifier_list().identifiers;
			for (Token token : identifiers) {
				String name = token.getText();
				if (MachineAnalyser.this.variablesDeclarations.containsKey(name)) {
					Token variableDeclaration = MachineAnalyser.this.variablesDeclarations.get(name);
					declarationReferences.put(token, variableDeclaration);
				} else {
					throw new ScopeException(token, "Identifier '" + name + "' must be a variable.");
				}
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
					declarationReferences.put(identifierToken, declarationToken);
					return;
				}
			}
			throw new ScopeException(identifierToken, "Unknown identifier: " + name);
		}

	}

}
