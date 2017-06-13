package de.bmoth.parser.cst;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.parser.cst.ScopeChecker.ScopeCheckerVisitorException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.bmoth.antlr.BMoThParser.*;

public class MachineAnalyser {

    private final StartContext parseTree;
    private final MachineScopeChecker machineScopeChecker;
    final LinkedHashMap<String, TerminalNode> constantsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, TerminalNode> variablesDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, TerminalNode> setsDeclarations = new LinkedHashMap<>();
    final List<EnumeratedSetContext> enumeratedSetContexts = new ArrayList<>();
    final List<DeferredSetContext> deferredSetContexts = new ArrayList<>();

    final LinkedHashMap<String, TerminalNode> definitionsDeclarations = new LinkedHashMap<>();

    final LinkedHashMap<TerminalNode, BDefinition> definitions = new LinkedHashMap<>();
    final LinkedHashMap<String, OperationContext> operationsDeclarations = new LinkedHashMap<>();
    final LinkedHashMap<String, EnumeratedSetContext> enumeratedSetElementsDeclarations = new LinkedHashMap<>();

    private PredicateClauseContext propertiesClause;
    private PredicateClauseContext invariantClause;
    private InitialisationClauseContext initialisationClause;

    private LinkedHashMap<TerminalNode, TerminalNode> declarationReferences;
    private final Map<ParserRuleContext, BDefinition> definitionCallReplacements = new HashMap<>();

    public MachineAnalyser(StartContext start) throws ScopeException {
        this.parseTree = start;
        this.machineScopeChecker = new MachineScopeChecker();
        try {
            // find and store all declaration of global identifiers
            new DeclarationFinder();

            // check that all used identifiers are declared
            // store a reference for each to identifier to its declaration
            checkScope();
        } catch (ScopeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(e.getClass().getName());
            logger.log(Level.SEVERE, "SCOPE_ERROR", e);
            throw e.getScopeException();
        }

    }

    public PredicateClauseContext getPropertiesClause() {
        return this.propertiesClause;
    }

    public PredicateClauseContext getInvariantClause() {
        return this.invariantClause;
    }

    public InitialisationClauseContext getInitialisationClause() {
        return this.initialisationClause;
    }

    public Map<ParserRuleContext, BDefinition> getDefinitionCallReplacements() {
        return this.definitionCallReplacements;
    }

    public List<TerminalNode> getConstants() {
        return new ArrayList<>(this.constantsDeclarations.values());
    }

    public List<TerminalNode> getVariables() {
        return new ArrayList<>(this.variablesDeclarations.values());
    }

    public List<EnumeratedSetContext> getEnumeratedSets() {
        return new ArrayList<>(this.enumeratedSetContexts);
    }

    public List<DeferredSetContext> getDeferredSetContexts() {
        return new ArrayList<>(this.deferredSetContexts);
    }

    public List<OperationContext> getOperations() {
        return new ArrayList<>(this.operationsDeclarations.values());
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
                kind = BDefinition.KIND.EXPRESSION;
            } else if (ctx.definition_body() instanceof DefinitionPredicateContext) {
                kind = BDefinition.KIND.PREDICATE;
            } else if (ctx.definition_body() instanceof DefinitionSubstitutionContext) {
                kind = BDefinition.KIND.SUBSTITUTION;
            } else if (ctx.definition_body() instanceof DefinitionAmbiguousCallContext) {
                kind = BDefinition.KIND.UNKNOWN;
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
                throw machineScopeChecker.new ScopeCheckerVisitorException(
                        new ScopeException("Duplicate declaration of identifier: " + name));
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
                if (MachineAnalyser.this.invariantClause == null) {
                    MachineAnalyser.this.invariantClause = ctx;
                } else {
                    throw machineScopeChecker.new ScopeCheckerVisitorException(
                            new ScopeException("Duplicate INVARIANT clause."));
                }
                break;
            case PROPERTIES:
                if (MachineAnalyser.this.propertiesClause == null) {
                    MachineAnalyser.this.propertiesClause = ctx;
                } else {
                    throw machineScopeChecker.new ScopeCheckerVisitorException(
                            new ScopeException("Duplicate PROPERTIES clause."));
                }
                break;
            default:
                unreachable();
            }
            return null;
        }

        @Override
        public Void visitInitialisationClause(BMoThParser.InitialisationClauseContext ctx) {
            if (MachineAnalyser.this.initialisationClause == null) {
                MachineAnalyser.this.initialisationClause = ctx;
            } else {
                throw machineScopeChecker.new ScopeCheckerVisitorException(
                        new ScopeException("Duplicate PROPERTIES clause."));
            }
            return null;
        }
    }

    class UnmatchedArgumentsQuantityException extends ScopeException {
        private static final long serialVersionUID = -2894774140796162549L;

        UnmatchedArgumentsQuantityException(BDefinition definition, int actual) {
            super("The number of parameters does not match the number of arguments of definition '"
                    + definition.getName() + "': " + actual + " vs " + definition.getArity());
        }

        UnmatchedArgumentsQuantityException(BDefinition definition) {
            super("Expecting " + definition.getArity() + " argument(s) for definition " + definition.getName());
        }
    }

    public Map<TerminalNode, TerminalNode> getDeclarationReferences() {
        return this.declarationReferences;
    }

    private void checkScope() {
        machineScopeChecker.check();
        this.declarationReferences = machineScopeChecker.declarationReferences;
    }

    class MachineScopeChecker extends ScopeChecker {

        MachineScopeChecker() {
        }

        void check() {
            if (MachineAnalyser.this.propertiesClause != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.propertiesClause.accept(this);
                scopeTable.clear();
            }

            if (MachineAnalyser.this.invariantClause != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.invariantClause.accept(this);
                scopeTable.clear();
            }

            if (MachineAnalyser.this.initialisationClause != null) {
                scopeTable.clear();
                scopeTable.add(MachineAnalyser.this.setsDeclarations);
                scopeTable.add(MachineAnalyser.this.constantsDeclarations);
                scopeTable.add(MachineAnalyser.this.variablesDeclarations);
                scopeTable.add(MachineAnalyser.this.definitionsDeclarations);
                MachineAnalyser.this.initialisationClause.accept(this);
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
            TerminalNode declarationNode = this.declarationReferences.get(terminalNode);
            if (definitions.containsKey(declarationNode)) {
                BDefinition bDefinition = definitions.get(declarationNode);
                if (bDefinition.getKind() == BDefinition.KIND.SUBSTITUTION
                        || bDefinition.getKind() == BDefinition.KIND.PREDICATE) {
                    throw new ScopeCheckerVisitorException(
                            new ScopeException("Expected a EXPRESSION definition but found a " + bDefinition.getKind()
                                    + " at definition " + bDefinition.getName()));
                }
                if (bDefinition.getArity() > 0) {
                    if (ctx.parent instanceof FunctionCallExpressionContext) {
                        FunctionCallExpressionContext funcCall = (FunctionCallExpressionContext) ctx.parent;
                        if (funcCall.exprs.size() - 1 != bDefinition.getArity()) {
                            throw new ScopeCheckerVisitorException(
                                    new UnmatchedArgumentsQuantityException(bDefinition, funcCall.exprs.size() - 1));
                        }
                        definitionCallReplacements.put(funcCall, bDefinition);
                    } else {
                        throw new ScopeCheckerVisitorException(new UnmatchedArgumentsQuantityException(bDefinition));
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
                    throw new ScopeCheckerVisitorException(
                            new UnmatchedArgumentsQuantityException(bDefinition, ctx.exprs.size()));
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
                    throw new ScopeCheckerVisitorException(new UnmatchedArgumentsQuantityException(bDefinition));
                }
                if (null != ctx.expression_list() && bDefinition.getArity() != ctx.expression_list().exprs.size()) {
                    throw new ScopeCheckerVisitorException(
                            new UnmatchedArgumentsQuantityException(bDefinition, ctx.expression_list().exprs.size()));
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
                if (bDefinition.getKind() == BDefinition.KIND.SUBSTITUTION
                        || bDefinition.getKind() == BDefinition.KIND.EXPRESSION) {
                    throw new ScopeCheckerVisitorException(
                            new ScopeException("Expected a PREDICATE definition but found a " + bDefinition.getKind()
                                    + " at definition " + bDefinition.getName()));
                }
                if (bDefinition.getArity() > 0) {
                    throw new ScopeCheckerVisitorException(new UnmatchedArgumentsQuantityException(bDefinition));
                }
                definitionCallReplacements.put(ctx, bDefinition);
            }
            return null;
        }

    }

}
