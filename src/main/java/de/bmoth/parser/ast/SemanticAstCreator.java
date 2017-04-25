package de.bmoth.parser.ast;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import de.bmoth.antlr.BMoThParser.FormulaContext;
import de.bmoth.antlr.BMoThParser.OperationContext;
import de.bmoth.antlr.BMoThParser.PredicateContext;
import de.bmoth.antlr.BMoThParser.SubstitutionContext;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.OperationNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitution;
import de.bmoth.parser.ast.nodes.SubstitutionNode;

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

		{
			List<OperationNode> operationsList = new ArrayList<>();
			for (Entry<String, OperationContext> entry : machineAnalyser.operationsDeclarations.entrySet()) {
				OperationContext operationContext = entry.getValue();
				SubstitutionNode substitution = (SubstitutionNode) operationContext.substitution()
						.accept(formulaVisitor);
				OperationNode operationNode = new OperationNode(entry.getValue(), entry.getKey(), substitution);
				operationsList.add(operationNode);
			}
			machineNode.setOperations(operationsList);
		}

		this.semanticNode = machineNode;
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
		public Node visitParenthesisPredicate(BMoThParser.ParenthesisPredicateContext ctx) {
			return ctx.predicate().accept(this);
		}

		@Override
		public Node visitParenthesesExpression(BMoThParser.ParenthesesExpressionContext ctx) {
			return ctx.expression().accept(this);
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
		public ExprNode visitNumberExpression(BMoThParser.NumberExpressionContext ctx) {
			int value = Integer.parseInt(ctx.Number().getText());
			return new NumberNode(ctx, value);
		}

		@Override
		public IdentifierExprNode visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
			return createIdentifierExprNode(ctx.IDENTIFIER().getSymbol());
		}

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
				SingleAssignSubstitution singleAssignSubstitution = new SingleAssignSubstitution(idents.get(i),
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
		public SelectSubstitutionNode visitSelectSubstitution(BMoThParser.SelectSubstitutionContext ctx) {
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
			ParallelSubstitutionNode paSub = new ParallelSubstitutionNode(result);
			return paSub;
		}

	}
}
