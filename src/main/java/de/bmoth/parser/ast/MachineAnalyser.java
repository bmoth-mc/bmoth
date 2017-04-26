package de.bmoth.parser.ast;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;

import static de.bmoth.antlr.BMoThParser.*;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.OperationContext;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.exceptions.ScopeException;

public class MachineAnalyser extends AbstractAnalyser {

	private final StartContext parseTree;

	final LinkedHashMap<String, Token> constantsDeclarations = new LinkedHashMap<>();
	final LinkedHashMap<String, Token> variablesDeclarations = new LinkedHashMap<>();
	final LinkedHashMap<String, OperationContext> operationsDeclarations = new LinkedHashMap<>();

	PredicateClauseContext properties;
	PredicateClauseContext invariant;
	InitialisationClauseContext initialisation;

	public MachineAnalyser(StartContext start) {
		this.parseTree = start;

		// find and store all declaration of global identifiers
		new DeclarationFinder();

		// check that all used identifiers are declared
		// store a reference for each to identifier to its declaration
		checkScope();

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

	private void checkScope() {
		ScopeChecker scopeChecker = new ScopeChecker(this);
		if (MachineAnalyser.this.properties != null) {
			scopeChecker.scopeTable.clear();
			scopeChecker.scopeTable.add(MachineAnalyser.this.constantsDeclarations);
			MachineAnalyser.this.properties.accept(scopeChecker);
			scopeChecker.scopeTable.clear();
		}

		if (MachineAnalyser.this.invariant != null) {
			scopeChecker.scopeTable.clear();
			scopeChecker.scopeTable.add(MachineAnalyser.this.constantsDeclarations);
			scopeChecker.scopeTable.add(MachineAnalyser.this.variablesDeclarations);
			MachineAnalyser.this.invariant.accept(scopeChecker);
			scopeChecker.scopeTable.clear();
		}

		if (MachineAnalyser.this.initialisation != null) {
			scopeChecker.scopeTable.clear();
			scopeChecker.scopeTable.add(MachineAnalyser.this.constantsDeclarations);
			scopeChecker.scopeTable.add(MachineAnalyser.this.variablesDeclarations);
			MachineAnalyser.this.initialisation.accept(scopeChecker);
			scopeChecker.scopeTable.clear();
		}

		for (Entry<String, OperationContext> entry : MachineAnalyser.this.operationsDeclarations.entrySet()) {
			scopeChecker.scopeTable.clear();
			scopeChecker.scopeTable.add(MachineAnalyser.this.constantsDeclarations);
			scopeChecker.scopeTable.add(MachineAnalyser.this.variablesDeclarations);
			entry.getValue().substitution().accept(scopeChecker);
			scopeChecker.scopeTable.clear();
		}
	}


	@Override
	public void identifierNodeFound(Token identifierToken) {
		throw new ScopeException(identifierToken, "Unknown identifier: " + identifierToken.getText());
	}

}
