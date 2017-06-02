package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.parser.ast.BDefinition.KIND;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.*;
import java.util.Map.Entry;

import static de.bmoth.antlr.BMoThParser.*;

public class MachineAnalyser {

    private final StartContext parseTree;

    final LinkedHashMap<String, Token> constantsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, Token> variablesDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, Token> setsDeclarations = new LinkedHashMap<>();
    final List<EnumeratedSetContext> enumeratedSetContexts = new ArrayList<>();
    final List<DeferredSetContext> deferredSetContexts = new ArrayList<>();
    final LinkedHashMap<String, Token> definitionsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<Token, BDefinition> definitions = new LinkedHashMap<>();
    final LinkedHashMap<String, OperationContext> operationsDeclarations = new LinkedHashMap<>();

    PredicateClauseContext properties;
    PredicateClauseContext invariant;
    InitialisationClauseContext initialisation;

    private LinkedHashMap<Token, Token> declarationReferences;
    final Map<ParserRuleContext, BDefinition> definitionCallReplacements = new HashMap<>();

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

        @Override
        public Void visitOrdinaryDefinition(OrdinaryDefinitionContext ctx) {
            Token nameToken = ctx.name;
            checkGlobalIdentifiers(nameToken);
            String name = nameToken.getText();
            definitionsDeclarations.put(name, nameToken);
            BDefinition.KIND kind = null;
            if (ctx.definition_body() instanceof DefinitionExpressionContext) {
                kind = KIND.EXPRESSION;
            } else if (ctx.definition_body() instanceof DefinitionPredicateContext) {
                kind = KIND.PREDICATE;
            } else if (ctx.definition_body() instanceof DefinitionSubstitutionContext) {
                kind = KIND.SUBSTITUTION;
            } else if (ctx.definition_body() instanceof DefinitionAmbiguousCallContext) {
                kind = KIND.UNKNOWN;
            }
            BDefinition bDefinition = new BDefinition(name, ctx, kind);
            definitions.put(nameToken, bDefinition);
            return null;
        }

        @Override
        public Void visitEnumeratedSet(BMoThParser.EnumeratedSetContext ctx) {
            enumeratedSetContexts.add(ctx);
            Token nameToken = ctx.IDENTIFIER().getSymbol();
            checkGlobalIdentifiers(nameToken);
            String name = nameToken.getText();
            setsDeclarations.put(name, nameToken);
            for (Token enumValue : ctx.identifier_list().identifiers) {
                checkGlobalIdentifiers(enumValue);
                setsDeclarations.put(enumValue.getText(), enumValue);
            }
            return null;
        }

        @Override
        public Void visitDeferredSet(BMoThParser.DeferredSetContext ctx) {
            deferredSetContexts.add(ctx);
            Token nameToken = ctx.IDENTIFIER().getSymbol();
            checkGlobalIdentifiers(nameToken);
            String name = nameToken.getText();
            setsDeclarations.put(name, nameToken);
            return null;
        }

        private void checkGlobalIdentifiers(Token token) {
            String name = token.getText();
            if (MachineAnalyser.this.constantsDeclarations.containsKey(name)
                || MachineAnalyser.this.variablesDeclarations.containsKey(name)
                || MachineAnalyser.this.operationsDeclarations.containsKey(name)
                || MachineAnalyser.this.setsDeclarations.containsKey(name)) {
                throw new ScopeException("Duplicate declaration of identifier: " + name);
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
                        throw new ScopeException("Duplicate INVARIANT clause.");
                    }
                    break;
                case PROPERTIES:
                    if (MachineAnalyser.this.properties == null) {
                        MachineAnalyser.this.properties = ctx;
                    } else {
                        throw new ScopeException("Duplicate PROPERTIES clause.");
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
                throw new ScopeException("Duplicate PROPERTIES clause.");
            }
            return null;
        }

    }

    public Map<Token, Token> getDeclarationReferences() {
        return this.declarationReferences;
    }

    private void checkScope() {
        MachineScopeChecker scopeChecker = new MachineScopeChecker();
        this.declarationReferences = scopeChecker.declarationReferences;
    }

    class MachineScopeChecker extends ScopeChecker {

        MachineScopeChecker() {
            if (MachineAnalyser.this.properties != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.properties.accept(this);
                scopeTable.clear();
            }

            if (MachineAnalyser.this.invariant != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.invariant.accept(this);
                scopeTable.clear();
            }

            if (MachineAnalyser.this.initialisation != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.initialisation.accept(this);
                scopeTable.clear();
            }

            for (Entry<String, OperationContext> entry : MachineAnalyser.this.operationsDeclarations.entrySet()) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                entry.getValue().substitution().accept(this);
                scopeTable.clear();
            }

            for (BDefinition bDef : MachineAnalyser.this.definitions.values()) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                LinkedHashMap<String, Token> localIdentifiers = new LinkedHashMap<>();
                for (Token token : bDef.getDefinitionContext().parameters) {
                    localIdentifiers.put(token.getText(), token);
                }
                scopeTable.add(localIdentifiers);
                bDef.getDefinitionContext().definition_body().accept(this);
                scopeTable.clear();
            }
        }

        @Override
        public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
            Token identifierToken = ctx.IDENTIFIER().getSymbol();
            lookUpToken(identifierToken);
            Token declarationToken = this.declarationReferences.get(identifierToken);
            if (definitions.containsKey(declarationToken)) {
                BDefinition bDefinition = definitions.get(declarationToken);
                if (bDefinition.getKind() == KIND.SUBSTITUTION || bDefinition.getKind() == KIND.PREDICATE) {
                    throw new ScopeException("Expected a EXPRESSION definition but found a " + bDefinition.getKind()
                        + " at definition " + bDefinition.getName());
                }
                if (bDefinition.getArity() > 0) {
                    if (ctx.parent instanceof FunctionCallExpressionContext) {
                        FunctionCallExpressionContext funcCall = (FunctionCallExpressionContext) ctx.parent;
                        if (funcCall.exprs.size() - 1 != bDefinition.getArity()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("The number of paramters does not match the number of arguments of definition '")
                                .append(bDefinition.getName()).append("'").append(": ")
                                .append(funcCall.exprs.size() - 1).append(" vs ").append(bDefinition.getArity());
                            throw new ScopeException(sb.toString());
                        }
                        definitionCallReplacements.put(funcCall, bDefinition);
                    } else {
                        throw new ScopeException("Expecting " + bDefinition.getArity() + " argument(s) for definition "
                            + bDefinition.getName());
                    }
                } else {
                    definitionCallReplacements.put(ctx, bDefinition);
                }
            }
            return null;
        }

        @Override
        public Void visitPredicateDefinitionCall(BMoThParser.PredicateDefinitionCallContext ctx) {
            Token identifierToken = ctx.IDENTIFIER().getSymbol();
            lookUpToken(identifierToken);
            Token declarationToken = this.declarationReferences.get(identifierToken);
            if (definitions.containsKey(declarationToken)) {
                BDefinition bDefinition = definitions.get(declarationToken);
                if (ctx.exprs.size() != bDefinition.getArity()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The number of paramters does not match the number of arguments of definition '")
                        .append(bDefinition.getName()).append("'").append(": ").append(ctx.exprs.size())
                        .append(" vs ").append(bDefinition.getArity());
                    throw new ScopeException(sb.toString());
                }
                definitionCallReplacements.put(ctx, bDefinition);

            }

            return null;
        }

        @Override
        public Void visitDefinitionAmbiguousCall(BMoThParser.DefinitionAmbiguousCallContext ctx) {
            Token identifierToken = ctx.IDENTIFIER().getSymbol();
            lookUpToken(identifierToken);
            Token declarationToken = this.declarationReferences.get(identifierToken);
            if (definitions.containsKey(declarationToken)) {
                BDefinition bDefinition = definitions.get(declarationToken);
                if (bDefinition.getArity() > 0 && null == ctx.expression_list()) {
                    throw new ScopeException("Expecting " + bDefinition.getArity() + " argument(s) for definition "
                        + bDefinition.getName());
                }
                if (null != ctx.expression_list() && bDefinition.getArity() != ctx.expression_list().exprs.size()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The number of paramters does not match the number of arguments of definition '")
                        .append(bDefinition.getName()).append("'").append(": ")
                        .append(ctx.expression_list().exprs.size()).append(" vs ").append(bDefinition.getArity());
                }
                definitionCallReplacements.put(ctx, bDefinition);
            }
            return visitChildren(ctx);
        }

        @Override
        public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
            Token identifierToken = ctx.IDENTIFIER().getSymbol();
            lookUpToken(identifierToken);
            Token declarationToken = this.declarationReferences.get(identifierToken);
            if (definitions.containsKey(declarationToken)) {
                BDefinition bDefinition = definitions.get(declarationToken);
                if (bDefinition.getKind() == KIND.SUBSTITUTION || bDefinition.getKind() == KIND.EXPRESSION) {
                    throw new ScopeException("Expected a PREDICATE definition but found a " + bDefinition.getKind()
                        + " at definition " + bDefinition.getName());
                }
                if (bDefinition.getArity() > 0) {
                    throw new ScopeException("Expecting " + bDefinition.getArity() + " argument(s) for definition "
                        + bDefinition.getName());
                }
                definitionCallReplacements.put(ctx, bDefinition);
            }
            return null;
        }

    }

}
