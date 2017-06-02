package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.exceptions.ScopeException;
import de.bmoth.parser.ast.BDefinition.KIND;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.bmoth.antlr.BMoThParser.*;

public class MachineAnalyser {

    private final StartContext parseTree;

    final LinkedHashMap<String, TerminalNode> constantsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, TerminalNode> variablesDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, TerminalNode> setsDeclarations = new LinkedHashMap<>();
    final List<EnumeratedSetContext> enumeratedSetContexts = new ArrayList<>();
    final List<DeferredSetContext> deferredSetContexts = new ArrayList<>();
    final LinkedHashMap<String, TerminalNode> definitionsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<TerminalNode, BDefinition> definitions = new LinkedHashMap<>();
    final LinkedHashMap<String, OperationContext> operationsDeclarations = new LinkedHashMap<>();

    PredicateClauseContext properties;
    PredicateClauseContext invariant;
    InitialisationClauseContext initialisation;

    private LinkedHashMap<TerminalNode, TerminalNode> declarationReferences;
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
            LinkedHashMap<String, TerminalNode> declarations = new LinkedHashMap<>();
            for (TerminalNode terminalNode : ctx.identifier_list().IDENTIFIER()) {
                checkGlobalIdentifiers(terminalNode);
                declarations.put(terminalNode.getSymbol().getText(), terminalNode);
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
            TerminalNode terminalNode = ctx.IDENTIFIER();
            checkGlobalIdentifiers(terminalNode);
            String name = terminalNode.getSymbol().getText();
            definitionsDeclarations.put(name, terminalNode);
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
            definitions.put(terminalNode, bDefinition);
            return null;
        }

        @Override
        public Void visitEnumeratedSet(BMoThParser.EnumeratedSetContext ctx) {
            enumeratedSetContexts.add(ctx);
            TerminalNode terminalNode = ctx.IDENTIFIER();
            Token nameToken = ctx.IDENTIFIER().getSymbol();
            checkGlobalIdentifiers(terminalNode);
            String name = nameToken.getText();
            setsDeclarations.put(name, terminalNode);
            for (TerminalNode enumValue : ctx.identifier_list().IDENTIFIER()) {
                checkGlobalIdentifiers(enumValue);
                setsDeclarations.put(enumValue.getSymbol().getText(), enumValue);
            }
            return null;
        }

        @Override
        public Void visitDeferredSet(BMoThParser.DeferredSetContext ctx) {
            deferredSetContexts.add(ctx);
            Token nameToken = ctx.IDENTIFIER().getSymbol();
            TerminalNode terminalNode = ctx.IDENTIFIER();
            checkGlobalIdentifiers(terminalNode);
            String name = nameToken.getText();
            setsDeclarations.put(name, terminalNode);
            return null;
        }

        private void checkGlobalIdentifiers(TerminalNode terminalNode) {
            String name = terminalNode.getSymbol().getText();
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
            TerminalNode terminalNode = ctx.IDENTIFIER();
            checkGlobalIdentifiers(terminalNode);
            String name = terminalNode.getSymbol().getText();
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

    public Map<TerminalNode, TerminalNode> getDeclarationReferences() {
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
                LinkedHashMap<String, TerminalNode> localIdentifiers = new LinkedHashMap<>();
                for (TerminalNode terminalNode : bDef.getParameters()) {
                    localIdentifiers.put(terminalNode.getSymbol().getText(), terminalNode);
                }
                scopeTable.add(localIdentifiers);
                bDef.getDefinitionContext().definition_body().accept(this);
                scopeTable.clear();
            }
        }

        @Override
        public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
            TerminalNode terminalNode = ctx.IDENTIFIER();
            lookUpTerminalNode(terminalNode);
            TerminalNode declarationTNode = this.declarationReferences.get(terminalNode);
            if (definitions.containsKey(declarationTNode)) {
                BDefinition bDefinition = definitions.get(declarationTNode);
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
            TerminalNode terminalNode = ctx.IDENTIFIER();
            lookUpTerminalNode(terminalNode);
            TerminalNode declarationTNode = this.declarationReferences.get(terminalNode);
            if (definitions.containsKey(declarationTNode)) {
                BDefinition bDefinition = definitions.get(declarationTNode);
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
            TerminalNode terminalNode = ctx.IDENTIFIER();
            lookUpTerminalNode(terminalNode);
            TerminalNode declarationTNode = this.declarationReferences.get(terminalNode);
            if (definitions.containsKey(declarationTNode)) {
                BDefinition bDefinition = definitions.get(declarationTNode);
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
            return null;
        }

        @Override
        public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
            TerminalNode terminalNode = ctx.IDENTIFIER();
            lookUpTerminalNode(terminalNode);
            TerminalNode declarationTNode = this.declarationReferences.get(terminalNode);
            if (definitions.containsKey(declarationTNode)) {
                BDefinition bDefinition = definitions.get(declarationTNode);
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
