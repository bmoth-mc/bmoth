package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.ltl.*;
import de.bmoth.parser.cst.BDefinition;
import de.bmoth.parser.cst.BDefinition.KIND;
import de.bmoth.parser.cst.FormulaAnalyser;
import de.bmoth.parser.cst.LTLFormulaAnalyser;
import de.bmoth.parser.cst.MachineAnalyser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

public class SemanticAstCreator {

    private final Map<TerminalNode, TerminalNode> declarationReferences;
    private final HashMap<TerminalNode, DeclarationNode> declarationMap = new HashMap<>();
    private final Map<ParserRuleContext, BDefinition> definitionCallReplacements;
    private final Map<TerminalNode, ExpressionContext> argumentReplacement = new HashMap<>();
    private final Map<EnumeratedSetContext, EnumeratedSetDeclarationNode> enumerations = new HashMap<>();
    private final Node semanticNode;

    public Node getAstNode() {
        return this.semanticNode;
    }

    public SemanticAstCreator(MachineAnalyser machineAnalyser) {
        this.declarationReferences = machineAnalyser.getDeclarationReferences();
        this.definitionCallReplacements = machineAnalyser.getDefinitionCallReplacements();

        MachineNode machineNode = new MachineNode(null, null);
        machineNode.setConstants(createDeclarationList(machineAnalyser.getConstants()));
        machineNode.setVariables(createDeclarationList(machineAnalyser.getVariables()));
        addEnumeratedSets(machineAnalyser.getEnumeratedSets(), machineNode);

        addDeferredSets(machineAnalyser.getDeferredSetContexts(), machineNode);

        FormulaVisitor formulaVisitor = new FormulaVisitor();

        if (null != machineAnalyser.getPropertiesClause()) {
            PredicateNode pred = (PredicateNode) machineAnalyser.getPropertiesClause().predicate()
                .accept(formulaVisitor);
            machineNode.setProperties(pred);
        }

        if (null != machineAnalyser.getInvariantClause()) {
            PredicateNode pred = (PredicateNode) machineAnalyser.getInvariantClause().predicate()
                .accept(formulaVisitor);
            machineNode.setInvariant(pred);
        }

        if (null != machineAnalyser.getInitialisationClause()) {
            SubstitutionNode substitution = (SubstitutionNode) machineAnalyser.getInitialisationClause().substitution()
                .accept(formulaVisitor);
            machineNode.setInitialisation(substitution);
        }

        Map<String, LtlStartContext> ltlFormulaMap = machineAnalyser.getLTLFormulaMap();
        for (Entry<String, LtlStartContext> entry : ltlFormulaMap.entrySet()) {
            String name = entry.getKey();
            LtlStartContext value = entry.getValue();
            LTLNode ltlNode = (LTLNode) value.accept(formulaVisitor);
            LTLFormula ltlFormula = new LTLFormula();
            ltlFormula.setName(name);
            ltlFormula.setFormula(ltlNode);
            machineNode.addLTLFormula(ltlFormula);
        }

        machineNode
            .setOperations(machineAnalyser.getOperations().stream()
                .map(op -> new OperationNode(op.IDENTIFIER().getText(),
                    (SubstitutionNode) op.substitution().accept(formulaVisitor)))
                .collect(Collectors.toList()));

        this.semanticNode = machineNode;

    }

    public SemanticAstCreator(FormulaAnalyser formulaAnalyser) {
        this.declarationReferences = formulaAnalyser.getDeclarationReferences();
        this.definitionCallReplacements = new LinkedHashMap<>();
        FormulaContext formulaContext = formulaAnalyser.getFormula();
        FormulaNode.FormulaType type = formulaContext.expression() != null ? EXPRESSION_FORMULA : PREDICATE_FORMULA;
        FormulaNode formulaNode = new FormulaNode(type);
        formulaNode.setImplicitDeclarations(createDeclarationList(formulaAnalyser.getImplicitDeclarations()));
        FormulaVisitor formulaVisitor = new FormulaVisitor();
        Node node;
        if (type == EXPRESSION_FORMULA) {
            node = formulaContext.expression().accept(formulaVisitor);
        } else {
            node = formulaContext.predicate().accept(formulaVisitor);
        }
        formulaNode.setFormula(node);
        this.semanticNode = formulaNode;
    }

    public SemanticAstCreator(LTLFormulaAnalyser formulaAnalyser) {
        this.declarationReferences = formulaAnalyser.getDeclarationReferences();
        this.definitionCallReplacements = new LinkedHashMap<>();
        FormulaVisitor formulaVisitor = new FormulaVisitor();
        LTLNode node = (LTLNode) formulaAnalyser.getLTLStartContext().ltlFormula().accept(formulaVisitor);
        LTLFormula ltlFormula = new LTLFormula();
        ltlFormula.setFormula(node);
        ltlFormula.setImplicitDeclarations(createDeclarationList(formulaAnalyser.getImplicitDeclarations()));
        this.semanticNode = ltlFormula;
    }

    private void addDeferredSets(List<DeferredSetContext> deferredSetContexts, MachineNode machineNode) {
        for (DeferredSetContext deferredSetContext : deferredSetContexts) {
            Token token = deferredSetContext.IDENTIFIER().getSymbol();
            DeclarationNode setDeclNode = new DeclarationNode(deferredSetContext.IDENTIFIER(), token.getText());
            declarationMap.put(deferredSetContext.IDENTIFIER(), setDeclNode);
            machineNode.addDeferredSet(setDeclNode);
        }
    }

    private List<DeclarationNode> createDeclarationList(List<TerminalNode> list) {
        List<DeclarationNode> declarationList = new ArrayList<>();
        for (TerminalNode terminalNode : list) {
            DeclarationNode declNode = new DeclarationNode(terminalNode, terminalNode.getSymbol().getText());
            declarationList.add(declNode);
            declarationMap.put(terminalNode, declNode);
        }
        return declarationList;
    }

    private List<DeclarationNode> createDeclarationList(Map<String, TerminalNode> map) {
        List<DeclarationNode> declarationList = new ArrayList<>();
        for (Entry<String, TerminalNode> entry : map.entrySet()) {
            TerminalNode terminalNode = entry.getValue();
            DeclarationNode declNode = new DeclarationNode(terminalNode, terminalNode.getSymbol().getText());
            declarationList.add(declNode);
            declarationMap.put(terminalNode, declNode);
        }
        return declarationList;
    }

    private List<DeclarationNode> createDeclarationNodeList(List<TerminalNode> list) {
        List<DeclarationNode> declarationList = new ArrayList<>();
        for (TerminalNode terminalNode : list) {
            Token token = terminalNode.getSymbol();
            DeclarationNode declNode = new DeclarationNode(terminalNode, token.getText());
            declarationList.add(declNode);
            declarationMap.put(terminalNode, declNode);
        }
        return declarationList;
    }

    private void addEnumeratedSets(List<EnumeratedSetContext> enumerationsContexts, MachineNode machineNode) {
        for (EnumeratedSetContext enumeratedSetContext : enumerationsContexts) {
            Token token = enumeratedSetContext.IDENTIFIER().getSymbol();
            DeclarationNode setDeclNode = new DeclarationNode(enumeratedSetContext.IDENTIFIER(), token.getText());
            declarationMap.put(enumeratedSetContext.IDENTIFIER(), setDeclNode);
            List<DeclarationNode> declarationList = createDeclarationNodeList(
                enumeratedSetContext.identifier_list().IDENTIFIER());
            EnumeratedSetDeclarationNode enumerationSet = new EnumeratedSetDeclarationNode(setDeclNode,
                declarationList);
            enumerations.put(enumeratedSetContext, enumerationSet);
            machineNode.addSetEnumeration(enumerationSet);
        }
    }

    class FormulaVisitor extends BMoThParserBaseVisitor<Node> {
        // TODO refactor definitions handling
        BDefinition.KIND currentKind;

        @Override
        public Node visitChildren(RuleNode node) {
            throw new AssertionError(node.getClass() + " is not implemented yet in semantic Ast creator.");
        }

        @Override
        public Node visitQuantifiedPredicate(BMoThParser.QuantifiedPredicateContext ctx) {
            List<DeclarationNode> declarationList = createDeclarationNodeList(
                ctx.quantified_variables_list().identifier_list().IDENTIFIER());
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            return new QuantifiedPredicateNode(ctx, declarationList, predNode);
        }

        @Override
        public Node visitEmptySequenceExpression(BMoThParser.EmptySequenceExpressionContext ctx) {
            return new ExpressionOperatorNode(ctx, new ArrayList<>(), ExpressionOperator.EMPTY_SEQUENCE);
        }

        @Override
        public Node visitSequenceEnumerationExpression(BMoThParser.SequenceEnumerationExpressionContext ctx) {
            if (ctx.expression_list() == null) {
                return new ExpressionOperatorNode(ctx, new ArrayList<>(), ExpressionOperator.EMPTY_SEQUENCE);
            } else {
                return new ExpressionOperatorNode(ctx, createExprNodeList(ctx.expression_list().expression()),
                    ExpressionOperator.SEQ_ENUMERATION);
            }
        }

        @Override
        public Node visitFunctionCallExpression(BMoThParser.FunctionCallExpressionContext ctx) {
            if (definitionCallReplacements.containsKey(ctx)) {
                currentKind = KIND.EXPRESSION;
                return replaceByDefinitionBody(ctx, definitionCallReplacements.get(ctx));
            } else {
                return new ExpressionOperatorNode(ctx, createExprNodeList(ctx.expression()),
                    ExpressionOperator.FUNCTION_CALL);
            }
        }

        @Override
        public ExprNode visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
            TerminalNode declNode = SemanticAstCreator.this.declarationReferences.get(ctx.IDENTIFIER());
            return handleExpressionIdentifier(ctx, ctx.IDENTIFIER(), declNode);
        }

        private ExprNode handleExpressionIdentifier(ParserRuleContext ctx, TerminalNode terminalNode,
                                                    TerminalNode declNode) {
            if (definitionCallReplacements.containsKey(ctx)) {
                currentKind = KIND.EXPRESSION;
                return (ExprNode) definitionCallReplacements.get(ctx).getDefinitionContext().definition_body()
                    .accept(this);
            } else if (argumentReplacement.containsKey(declNode)) {
                ExpressionContext expressionContext = argumentReplacement.get(declNode);
                return (ExprNode) expressionContext.accept(this);
            } else if (declNode.getParent() instanceof EnumeratedSetContext) {
                EnumeratedSetDeclarationNode enumeratedSetDeclarationNode = enumerations.get(declNode.getParent());
                return new EnumerationSetNode(terminalNode, enumeratedSetDeclarationNode, terminalNode.getText());
            } else if (declNode.getParent() instanceof DeferredSetContext) {
                DeclarationNode declarationNode = declarationMap.get(declNode);
                return new DeferredSetNode(terminalNode, declarationNode, terminalNode.getText());
            } else if (declNode.getParent().getParent() instanceof EnumeratedSetContext) {
                EnumeratedSetDeclarationNode enumeratedSetDeclarationNode = enumerations.get(declNode.getParent());
                DeclarationNode declarationNode = declarationMap.get(declNode);
                return new EnumeratedSetElementNode(terminalNode, enumeratedSetDeclarationNode, terminalNode.getText(),
                    declarationNode);
            } else {
                return createIdentifierExprNode(terminalNode);
            }
        }

        @Override
        public Node visitDefinitionAmbiguousCall(BMoThParser.DefinitionAmbiguousCallContext ctx) {
            TerminalNode declToken = SemanticAstCreator.this.declarationReferences.get(ctx.IDENTIFIER());
            if (currentKind == KIND.EXPRESSION) {
                if (null != ctx.expression_list()) {
                    List<ExpressionContext> exprs = ctx.expression_list().exprs;
                    if (definitionCallReplacements.containsKey(ctx)) {
                        currentKind = KIND.EXPRESSION;
                        BDefinition bDefinition = definitionCallReplacements.get(ctx);
                        for (ExpressionContext value : exprs) {
                            TerminalNode terminalNode = bDefinition.getDefinitionContext().IDENTIFIER();
                            argumentReplacement.put(terminalNode, value);
                        }
                        return bDefinition.getDefinitionContext().definition_body().accept(this);
                    } else {
                        List<ExprNode> exprNodes = new ArrayList<>();
                        exprNodes.add(createIdentifierExprNode(ctx.IDENTIFIER()));
                        return new ExpressionOperatorNode(ctx, exprNodes, ExpressionOperator.FUNCTION_CALL);
                    }
                } else {
                    return handleExpressionIdentifier(ctx, ctx.IDENTIFIER(), declToken);
                }

            } else if (currentKind == KIND.PREDICATE) {
                return handlePredicateIdentifier(ctx, ctx.IDENTIFIER());
            }
            return visitChildren(ctx);
        }

        @Override
        public ExprNode visitDefinitionExpression(BMoThParser.DefinitionExpressionContext ctx) {
            return (ExprNode) ctx.expression().accept(this);
        }

        @Override
        public PredicateNode visitDefinitionPredicate(BMoThParser.DefinitionPredicateContext ctx) {
            return (PredicateNode) ctx.predicate().accept(this);
        }

        @Override
        public SubstitutionNode visitDefinitionSubstitution(BMoThParser.DefinitionSubstitutionContext ctx) {
            return (SubstitutionNode) ctx.substitution().accept(this);
        }

        @Override
        public PredicateNode visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
            currentKind = KIND.PREDICATE;
            return handlePredicateIdentifier(ctx, ctx.IDENTIFIER());
        }

        private PredicateNode handlePredicateIdentifier(ParserRuleContext ctx, TerminalNode terminalNode) {
            if (definitionCallReplacements.containsKey(ctx)) {
                BDefinition bDefinition = definitionCallReplacements.get(ctx);
                return (PredicateNode) bDefinition.getDefinitionContext().definition_body().accept(this);
            } else {
                return createIdentifierPredicateNode(terminalNode);
            }
        }

        private Node replaceByDefinitionBody(FunctionCallExpressionContext ctx, BDefinition bDefinition) {
            for (int i = 1; i < ctx.exprs.size(); i++) {
                ExpressionContext value = ctx.exprs.get(i);
                TerminalNode terminalNode = bDefinition.getDefinitionContext().identifier_list().IDENTIFIER()
                    .get(i - 1);
                argumentReplacement.put(terminalNode, value);
            }
            return bDefinition.getDefinitionContext().definition_body().accept(this);
        }

        @Override
        public PredicateNode visitPredicateDefinitionCall(BMoThParser.PredicateDefinitionCallContext ctx) {
            BDefinition bDefinition = definitionCallReplacements.get(ctx);
            for (int i = 0; i < ctx.expression().size(); i++) {
                ExpressionContext value = ctx.exprs.get(i);
                TerminalNode terminalNode = bDefinition.getDefinitionContext().identifier_list().IDENTIFIER(i);
                argumentReplacement.put(terminalNode, value);
            }
            DefinitionPredicateContext defContext = (DefinitionPredicateContext) bDefinition.getDefinitionContext()
                .definition_body();
            return (PredicateNode) defContext.predicate().accept(this);
        }

        @Override
        public Node visitParenthesesPredicate(BMoThParser.ParenthesesPredicateContext ctx) {
            return ctx.predicate().accept(this);
        }

        @Override
        public Node visitParenthesesExpression(BMoThParser.ParenthesesExpressionContext ctx) {
            return ctx.expression().accept(this);
        }

        @Override
        public Node visitCastPredicateExpression(BMoThParser.CastPredicateExpressionContext ctx) {
            // internally, we do not distinguish bools and predicates
            PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
            return new CastPredicateExpressionNode(ctx, predicate);
        }

        @Override
        public Node visitQuantifiedExpression(BMoThParser.QuantifiedExpressionContext ctx) {
            List<DeclarationNode> declarationList = createDeclarationNodeList(
                ctx.quantified_variables_list().identifier_list().IDENTIFIER());
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            ExprNode exprNode = (ExprNode) ctx.expression().accept(this);
            return new QuantifiedExpressionNode(ctx, declarationList, predNode, exprNode, ctx.operator);
        }

        @Override
        public Node visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
            List<DeclarationNode> declarationList = createDeclarationNodeList(ctx.identifier_list().IDENTIFIER());
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            return new SetComprehensionNode(ctx, declarationList, predNode);
        }

        @Override
        public Node visitNestedCoupleAsTupleExpression(BMoThParser.NestedCoupleAsTupleExpressionContext ctx) {
            List<ExpressionContext> exprs = ctx.exprs;
            ExprNode left = (ExprNode) exprs.get(0).accept(this);
            for (int i = 1; i < exprs.size(); i++) {
                List<ExprNode> list = new ArrayList<>();
                list.add(left);
                list.add((ExprNode) exprs.get(i).accept(this));
                left = new ExpressionOperatorNode(ctx, list, ExpressionOperator.COUPLE);
            }
            return left;
        }

        @Override
        public ExpressionOperatorNode visitExpressionOperator(BMoThParser.ExpressionOperatorContext ctx) {
            String operator = ctx.operator.getText();
            return new ExpressionOperatorNode(ctx, createExprNodeList(ctx.expression()), operator);
        }

        @Override
        public ExprNode visitSetEnumerationExpression(BMoThParser.SetEnumerationExpressionContext ctx) {
            return new ExpressionOperatorNode(ctx, createExprNodeList(ctx.expression_list().expression()),
                ExpressionOperator.SET_ENUMERATION);
        }

        @Override
        public ExprNode visitEmptySetExpression(BMoThParser.EmptySetExpressionContext ctx) {
            return new ExpressionOperatorNode(ctx, new ArrayList<>(), ExpressionOperator.EMPTY_SET);
        }

        @Override
        public ExprNode visitNumberExpression(BMoThParser.NumberExpressionContext ctx) {
            BigInteger value = new BigInteger(ctx.Number().getText());
            return new NumberNode(ctx, value);
        }

        // Predicates
        @Override
        public PredicateNode visitPredicateOperator(BMoThParser.PredicateOperatorContext ctx) {
            List<PredicateNode> list = new ArrayList<>();
            List<PredicateContext> predicate = ctx.predicate();
            for (PredicateContext predicateContext : predicate) {
                PredicateNode predNode = (PredicateNode) predicateContext.accept(this);
                list.add(predNode);
            }
            return new PredicateOperatorNode(ctx, list);
        }

        @Override
        public PredicateNode visitPredicateOperatorWithExprArgs(BMoThParser.PredicateOperatorWithExprArgsContext ctx) {
            return new PredicateOperatorWithExprArgsNode(ctx, createExprNodeList(ctx.expression()));
        }

        private List<ExprNode> createExprNodeList(List<ExpressionContext> list) {
            ArrayList<ExprNode> exprNodes = new ArrayList<>();
            for (ExpressionContext expressionContext : list) {
                ExprNode exprNode = (ExprNode) expressionContext.accept(this);
                exprNodes.add(exprNode);
            }
            return exprNodes;
        }

        // Substitutions

        @Override
        public Node visitBlockSubstitution(BMoThParser.BlockSubstitutionContext ctx) {
            return ctx.substitution().accept(this);
        }

        @Override
        public SubstitutionNode visitAssignSubstitution(BMoThParser.AssignSubstitutionContext ctx) {
            List<IdentifierExprNode> idents = ctx.identifier_list().IDENTIFIER().stream()
                .map(this::createIdentifierExprNode).collect(Collectors.toList());

            List<ExprNode> expressions = ctx.expression_list().exprs.stream().map(t -> (ExprNode) t.accept(this))
                .collect(Collectors.toList());

            List<SubstitutionNode> sublist = IntStream.range(0, idents.size())
                .mapToObj(t -> new SingleAssignSubstitutionNode(idents.get(t), expressions.get(t)))
                .collect(Collectors.toList());
            if (sublist.size() == 1) {
                return sublist.get(0);
            } else {
                return new ParallelSubstitutionNode(sublist);
            }
        }

        @Override
        public SubstitutionNode visitBecomesElementOfSubstitution(BMoThParser.BecomesElementOfSubstitutionContext ctx) {
            List<IdentifierExprNode> idents = ctx.identifier_list().IDENTIFIER().stream()
                .map(this::createIdentifierExprNode).collect(Collectors.toList());
            ExprNode expression = (ExprNode) ctx.expression().accept(this);
            return new BecomesElementOfSubstitutionNode(idents, expression);
        }

        @Override
        public SubstitutionNode visitBecomesSuchThatSubstitution(BMoThParser.BecomesSuchThatSubstitutionContext ctx) {
            List<IdentifierExprNode> idents = ctx.identifier_list().IDENTIFIER().stream()
                .map(this::createIdentifierExprNode).collect(Collectors.toList());
            PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
            return new BecomesSuchThatSubstitutionNode(idents, predicate);
        }

        @Override
        public SubstitutionNode visitAnySubstitution(BMoThParser.AnySubstitutionContext ctx) {
            List<DeclarationNode> declarationList = createDeclarationNodeList(ctx.identifier_list().IDENTIFIER());
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
            return new AnySubstitutionNode(declarationList, predNode, sub);
        }

        @Override
        public SelectSubstitutionNode visitSelectSubstitution(BMoThParser.SelectSubstitutionContext ctx) {
            List<PredicateNode> predNodes = ctx.preds.stream().map(t -> (PredicateNode) t.accept(this))
                .collect(Collectors.toList());
            List<SubstitutionNode> subNodes = ctx.subs.stream().map(t -> (SubstitutionNode) t.accept(this))
                .collect(Collectors.toList());
            SubstitutionNode elseSubNode = null;
            if (ctx.elseSub != null) {
                elseSubNode = (SubstitutionNode) ctx.elseSub.accept(this);
            }
            return new SelectSubstitutionNode(predNodes, subNodes, elseSubNode);
        }

        @Override
        public SubstitutionNode visitIfSubstitution(BMoThParser.IfSubstitutionContext ctx) {
            List<PredicateNode> predNodes = ctx.preds.stream().map(t -> (PredicateNode) t.accept(this))
                .collect(Collectors.toList());
            List<SubstitutionNode> subNodes = ctx.subs.stream().map(t -> (SubstitutionNode) t.accept(this))
                .collect(Collectors.toList());
            SubstitutionNode elseSubNode = null;
            if (ctx.elseSub != null) {
                elseSubNode = (SubstitutionNode) ctx.elseSub.accept(this);
            }
            return new IfSubstitutionNode(predNodes, subNodes, elseSubNode);
        }

        @Override
        public ConditionSubstitutionNode visitConditionSubstitution(BMoThParser.ConditionSubstitutionContext ctx) {
            PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
            SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
            if (ctx.keyword.getType() == BMoThParser.PRE) {
                return new ConditionSubstitutionNode(ConditionSubstitutionNode.Kind.PRECONDITION, predicate, sub);
            } else {
                return new ConditionSubstitutionNode(ConditionSubstitutionNode.Kind.ASSERT, predicate, sub);
            }

        }

        private IdentifierExprNode createIdentifierExprNode(TerminalNode terminalNode) {
            Token token = terminalNode.getSymbol();
            TerminalNode declNode = SemanticAstCreator.this.declarationReferences.get(terminalNode);
            DeclarationNode declarationNode = declarationMap.get(declNode);
            if (declarationNode == null) {
                throw new AssertionError("Can not find declaration node of identifier '" + token.getText() + "' Line "
                    + token.getLine() + " Pos " + token.getCharPositionInLine());
            }
            return new IdentifierExprNode(terminalNode, declarationNode);
        }

        private IdentifierPredicateNode createIdentifierPredicateNode(TerminalNode terminalNode) {
            Token token = terminalNode.getSymbol();
            TerminalNode declNode = SemanticAstCreator.this.declarationReferences.get(terminalNode);
            DeclarationNode declarationNode = declarationMap.get(declNode);
            if (declarationNode == null) {
                throw new AssertionError(token.getText() + " Line " + token.getLine());
            }
            return new IdentifierPredicateNode(terminalNode, declarationNode);
        }

        @Override
        public SubstitutionNode visitSkipSubstitution(BMoThParser.SkipSubstitutionContext ctx) {
            return new SkipSubstitutionNode();
        }

        @Override
        public SubstitutionNode visitParallelSubstitution(BMoThParser.ParallelSubstitutionContext ctx) {
            List<SubstitutionNode> result = new ArrayList<>();
            List<SubstitutionContext> substitution = ctx.substitution();
            for (SubstitutionContext substitutionContext : substitution) {
                SubstitutionNode sub = (SubstitutionNode) substitutionContext.accept(this);
                result.add(sub);
            }
            return new ParallelSubstitutionNode(result);
        }

        // LTL

        @Override
        public LTLNode visitLtlStart(BMoThParser.LtlStartContext ctx) {
            return (LTLNode) ctx.ltlFormula().accept(this);
        }

        @Override
        public Node visitLTLPrefixOperator(BMoThParser.LTLPrefixOperatorContext ctx) {
            LTLNode argument = (LTLNode) ctx.ltlFormula().accept(this);
            LTLPrefixOperatorNode.Kind kind = null;
            switch (ctx.operator.getType()) {
                case BMoThParser.LTL_GLOBALLY:
                    kind = LTLPrefixOperatorNode.Kind.GLOBALLY;
                    break;
                case BMoThParser.LTL_FINALLY:
                    kind = LTLPrefixOperatorNode.Kind.FINALLY;
                    break;
                case BMoThParser.LTL_NEXT:
                    kind = LTLPrefixOperatorNode.Kind.NEXT;
                    break;
                case BMoThParser.LTL_NOT:
                    kind = LTLPrefixOperatorNode.Kind.NOT;
                    break;
                default:
                    throw new AssertionError();
            }
            return new LTLPrefixOperatorNode(kind, argument);
        }

        @Override
        public Node visitLTLKeyword(BMoThParser.LTLKeywordContext ctx) {
            LTLKeywordNode.Kind kind = null;
            switch (ctx.keyword.getType()) {
                case BMoThParser.LTL_TRUE:
                    kind = LTLKeywordNode.Kind.TRUE;
                    break;
                case BMoThParser.LTL_FALSE:
                    kind = LTLKeywordNode.Kind.FALSE;
                    break;
                default:
                    throw new AssertionError();
            }
            return new LTLKeywordNode(kind);
        }

        @Override
        public Node visitLTLBPredicate(BMoThParser.LTLBPredicateContext ctx) {
            PredicateNode node = (PredicateNode) ctx.predicate().accept(this);
            return new LTLBPredicateNode(node);
        }

        @Override
        public Node visitLTLParentheses(BMoThParser.LTLParenthesesContext ctx) {
            return ctx.ltlFormula().accept(this);
        }

        @Override
        public Node visitLTLInfixOperator(BMoThParser.LTLInfixOperatorContext ctx) {
            LTLNode left = (LTLNode) ctx.ltlFormula(0).accept(this);
            LTLNode right = (LTLNode) ctx.ltlFormula(1).accept(this);
            LTLInfixOperatorNode.Kind kind = null;
            switch (ctx.operator.getType()) {
                case BMoThParser.LTL_IMPLIES:
                    kind = LTLInfixOperatorNode.Kind.IMPLICATION;
                    break;
                case BMoThParser.LTL_UNTIL:
                    kind = LTLInfixOperatorNode.Kind.UNTIL;
                    break;
                case BMoThParser.LTL_WEAK_UNTIL:
                    kind = LTLInfixOperatorNode.Kind.WEAK_UNTIL;
                    break;
                case BMoThParser.LTL_RELEASE:
                    kind = LTLInfixOperatorNode.Kind.RELEASE;
                    break;
                case BMoThParser.LTL_AND:
                    kind = LTLInfixOperatorNode.Kind.AND;
                    break;
                case BMoThParser.LTL_OR:
                    kind = LTLInfixOperatorNode.Kind.OR;
                    break;
                default:
                    throw new AssertionError();
            }
            return new LTLInfixOperatorNode(kind, left, right);
        }

    }
}
