package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode.QuatifiedExpressionOperator;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

public class SemanticAstCreator {

    private final LinkedHashMap<Token, Token> declarationReferences;
    private final HashMap<Token, DeclarationNode> declarationMap = new HashMap<>();
    private final Node semanticNode;

    public Node getAstNode() {
        return this.semanticNode;
    }

    public SemanticAstCreator(FormulaAnalyser formulaAnalyser) {
        this.declarationReferences = formulaAnalyser.declarationReferences;
        FormulaContext formulaContext = formulaAnalyser.formula;
        FormulaNode.FormulaType type = formulaContext.expression() != null ? EXPRESSION_FORMULA : PREDICATE_FORMULA;
        FormulaNode formulaNode = new FormulaNode(type);
        formulaNode.setImplicitDeclarations(createDeclarationList(formulaAnalyser.implicitDeclarations));
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

    public SemanticAstCreator(MachineAnalyser machineAnalyser) {
        this.declarationReferences = machineAnalyser.declarationReferences;
        MachineNode machineNode = new MachineNode(null, null);
        machineNode.setConstants(createDeclarationList(machineAnalyser.constantsDeclarations));
        machineNode.setVariables(createDeclarationList(machineAnalyser.variablesDeclarations));
        addEnumeratedSets(machineAnalyser.enumeratedSetContexts, machineNode);
        addDeferredSets(machineAnalyser.deferredSetContexts, machineNode);

        FormulaVisitor formulaVisitor = new FormulaVisitor();

        if (machineAnalyser.properties != null) {
            PredicateNode pred = (PredicateNode) machineAnalyser.properties.predicate().accept(formulaVisitor);
            machineNode.setProperties(pred);
        }

        if (machineAnalyser.invariant != null) {
            PredicateNode pred = (PredicateNode) machineAnalyser.invariant.predicate().accept(formulaVisitor);
            machineNode.setInvariant(pred);
        }

        if (machineAnalyser.initialisation != null) {
            SubstitutionNode substitution = (SubstitutionNode) machineAnalyser.initialisation.substitution()
                .accept(formulaVisitor);
            machineNode.setInitialisation(substitution);
        }

        List<OperationNode> operationsList = new ArrayList<>();
        for (Entry<String, OperationContext> entry : machineAnalyser.operationsDeclarations.entrySet()) {
            OperationContext operationContext = entry.getValue();
            SubstitutionNode substitution = (SubstitutionNode) operationContext.substitution().accept(formulaVisitor);
            OperationNode operationNode = new OperationNode(entry.getValue(), entry.getKey(), substitution);
            operationsList.add(operationNode);
        }
        machineNode.setOperations(operationsList);

        this.semanticNode = machineNode;

    }

    private void addDeferredSets(List<DeferredSetContext> deferredSetContexts, MachineNode machineNode) {
        for (DeferredSetContext deferredSetContext : deferredSetContexts) {
            Token token = deferredSetContext.IDENTIFIER().getSymbol();
            DeclarationNode setDeclNode = new DeclarationNode(token, token.getText());
            declarationMap.put(token, setDeclNode);
            machineNode.addDeferredSet(setDeclNode);
        }
    }

    private void addEnumeratedSets(List<EnumeratedSetContext> enumerationsContexts, MachineNode machineNode) {
        for (EnumeratedSetContext enumeratedSetContext : enumerationsContexts) {
            Token token = enumeratedSetContext.IDENTIFIER().getSymbol();
            DeclarationNode setDeclNode = new DeclarationNode(token, token.getText());
            declarationMap.put(token, setDeclNode);
            List<DeclarationNode> list = new ArrayList<>();
            for (Token element : enumeratedSetContext.identifier_list().identifiers) {
                DeclarationNode declNode = new DeclarationNode(element, element.getText());
                list.add(declNode);
                declarationMap.put(element, declNode);
            }
            EnumeratedSet setEnumeration = new EnumeratedSet(setDeclNode, list);
            machineNode.addSetEnumeration(setEnumeration);
        }
    }

    private List<DeclarationNode> createDeclarationList(LinkedHashMap<String, Token> constantsDeclarations) {
        List<DeclarationNode> declarationList = new ArrayList<>();
        for (Entry<String, Token> entry : constantsDeclarations.entrySet()) {
            Token token = entry.getValue();
            DeclarationNode declNode = new DeclarationNode(token, entry.getKey());
            declarationList.add(declNode);
            declarationMap.put(token, declNode);
        }
        return declarationList;
    }

    class FormulaVisitor extends BMoThParserBaseVisitor<Node> {

        @Override
        public Node visitChildren(RuleNode node) {
            throw new AssertionError(node.getClass() + " is not implemented yet in semantic Ast creator.");
        }

        @Override
        public Node visitQuantifiedPredicate(BMoThParser.QuantifiedPredicateContext ctx) {
            List<Token> identifiers = ctx.quantified_variables_list().identifier_list().identifiers;
            List<DeclarationNode> declarationList = new ArrayList<>();
            for (Token token : identifiers) {
                DeclarationNode declNode = new DeclarationNode(token, token.getText());
                declarationList.add(declNode);
                declarationMap.put(token, declNode);
            }
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            return new QuantifiedPredicateNode(ctx, declarationList, predNode);
        }

        @Override
        public Node visitSequenceEnumerationExpression(BMoThParser.SequenceEnumerationExpressionContext ctx) {
            if (ctx.expression_list() == null) {
                return new ExpressionOperatorNode(new ArrayList<>(), ExpressionOperator.EMPTY_SEQUENCE);
            } else {
                return new ExpressionOperatorNode(createExprNodeList(ctx.expression_list().expression()),
                    ExpressionOperator.SEQ_ENUMERATION);
            }
        }

        @Override
        public Node visitFunctionCallExpression(BMoThParser.FunctionCallExpressionContext ctx) {
            return new ExpressionOperatorNode(createExprNodeList(ctx.expression()),
                ExpressionOperator.FUNCTION_CALL);
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
            return new CastPredicateExpressionNode(predicate);
        }

        @Override
        public Node visitQuantifiedExpression(BMoThParser.QuantifiedExpressionContext ctx) {
            List<Token> identifiers = ctx.quantified_variables_list().identifier_list().identifiers;
            List<DeclarationNode> declarationList = new ArrayList<>();
            for (Token token : identifiers) {
                DeclarationNode declNode = new DeclarationNode(token, token.getText());
                declarationList.add(declNode);
                declarationMap.put(token, declNode);
            }
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            ExprNode exprNode = (ExprNode) ctx.expression().accept(this);
            return new QuantifiedExpressionNode(ctx, declarationList, predNode, exprNode, ctx.operator);
        }

        @Override
        public Node visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
            List<Token> identifiers = ctx.identifier_list().identifiers;
            List<DeclarationNode> declarationList = new ArrayList<>();
            for (Token token : identifiers) {
                DeclarationNode declNode = new DeclarationNode(token, token.getText());
                declarationList.add(declNode);
                declarationMap.put(token, declNode);
            }
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            return new QuantifiedExpressionNode(ctx, declarationList, predNode, null,
                QuatifiedExpressionOperator.SET_COMPREHENSION);
        }

        @Override
        public Node visitNestedCoupleAsTupleExpression(BMoThParser.NestedCoupleAsTupleExpressionContext ctx) {
            List<ExpressionContext> exprs = ctx.exprs;
            ExprNode left = (ExprNode) exprs.get(0).accept(this);
            for (int i = 1; i < exprs.size(); i++) {
                List<ExprNode> list = new ArrayList<>();
                list.add(left);
                list.add((ExprNode) exprs.get(i).accept(this));
                left = new ExpressionOperatorNode(list, ExpressionOperator.COUPLE);
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
            return new ExpressionOperatorNode(createExprNodeList(ctx.expression_list().expression()),
                ExpressionOperator.SET_ENUMERATION);
        }

        @Override
        public ExprNode visitEmptySetExpression(BMoThParser.EmptySetExpressionContext ctx) {
            return new ExpressionOperatorNode(new ArrayList<>(), ExpressionOperator.EMPTY_SET);
        }

        @Override
        public ExprNode visitNumberExpression(BMoThParser.NumberExpressionContext ctx) {
            int value = Integer.parseInt(ctx.Number().getText());
            return new NumberNode(ctx, value);
        }

        @Override
        public IdentifierExprNode visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
            return createIdentifierExprNode(ctx.IDENTIFIER().getSymbol());
        }

        @Override
        public IdentifierPredicateNode visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
            return createIdentifierPredicateNode(ctx.IDENTIFIER().getSymbol());
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
            List<IdentifierExprNode> idents = new ArrayList<>();
            List<Token> identifierTokens = ctx.identifier_list().identifiers;
            for (Token token : identifierTokens) {
                IdentifierExprNode identExprNode = createIdentifierExprNode(token);
                idents.add(identExprNode);
            }

            List<ExprNode> expressions = new ArrayList<>();
            List<ExpressionContext> exprsContexts = ctx.expression_list().exprs;
            for (ExpressionContext expressionContext : exprsContexts) {
                ExprNode exprNode = (ExprNode) expressionContext.accept(this);
                expressions.add(exprNode);
            }

            List<SubstitutionNode> sublist = new ArrayList<>();
            for (int i = 0; i < idents.size(); i++) {
                SingleAssignSubstitutionNode singleAssignSubstitution = new SingleAssignSubstitutionNode(idents.get(i),
                    expressions.get(i));
                sublist.add(singleAssignSubstitution);
            }
            if (sublist.size() == 1) {
                return sublist.get(0);
            } else {
                return new ParallelSubstitutionNode(sublist);
            }
        }

        @Override
        public SubstitutionNode visitAnySubstitution(BMoThParser.AnySubstitutionContext ctx) {
            List<Token> identifiers = ctx.identifier_list().identifiers;
            List<DeclarationNode> declarationList = new ArrayList<>();
            for (Token token : identifiers) {
                DeclarationNode declNode = new DeclarationNode(token, token.getText());
                declarationList.add(declNode);
                declarationMap.put(token, declNode);
            }
            PredicateNode predNode = (PredicateNode) ctx.predicate().accept(this);
            SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
            return new AnySubstitutionNode(declarationList, predNode, sub);
        }

        @Override
        public SelectSubstitutionNode visitSelectSubstitution(BMoThParser.SelectSubstitutionContext ctx) {
            PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
            SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
            return new SelectSubstitutionNode(predicate, sub);
        }

        @Override
        public SelectSubstitutionNode visitPreSubstitution(BMoThParser.PreSubstitutionContext ctx) {
            PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
            SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
            return new SelectSubstitutionNode(predicate, sub);
        }

        private IdentifierExprNode createIdentifierExprNode(Token token) {
            Token declToken = SemanticAstCreator.this.declarationReferences.get(token);
            DeclarationNode declarationNode = declarationMap.get(declToken);
            if (declarationNode == null) {
                throw new AssertionError(token.getText() + " Line " + token.getLine());
            }
            return new IdentifierExprNode(token, declarationNode);
        }

        private IdentifierPredicateNode createIdentifierPredicateNode(Token token) {
            Token declToken = SemanticAstCreator.this.declarationReferences.get(token);
            DeclarationNode declarationNode = declarationMap.get(declToken);
            if (declarationNode == null) {
                throw new AssertionError(token.getText() + " Line " + token.getLine());
            }
            return new IdentifierPredicateNode(token, declarationNode);
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

    }
}
