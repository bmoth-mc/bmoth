package de.bmoth.parser.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import de.bmoth.antlr.BMoThParser.OperationContext;
import de.bmoth.antlr.BMoThParser.PredicateContext;
import de.bmoth.antlr.BMoThParser.SubstitutionContext;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.antlr.BMoThParserBaseVisitor;

public class SemanticAstCreator {

	final MachineAnalyser machineAnalyser;
	final MachineNode machineNode;

	HashMap<Token, DeclarationNode> declarationMap = new HashMap<>();

	public SemanticAstCreator(MachineAnalyser machineAnalyser) {
		this.machineAnalyser = machineAnalyser;
		this.machineNode = new MachineNode(null, null);
		this.machineNode.setConstants(createDeclarationList(machineAnalyser.constantsDeclarations));
		this.machineNode.setVariables(createDeclarationList(machineAnalyser.variablesDeclarations));

		FormulaVisitor formulaVisitor = new FormulaVisitor();

		if (machineAnalyser.invariant != null) {
			PredicateNode pred = (PredicateNode) machineAnalyser.invariant.accept(formulaVisitor);
			this.machineNode.setInvariant(pred);
		}

		if (machineAnalyser.initialisation != null) {
			SubstitutionNode substitution = (SubstitutionNode) machineAnalyser.initialisation.substitution()
					.accept(formulaVisitor);
			this.machineNode.setInitialisation(substitution);
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
			this.machineNode.setOperations(operationsList);
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
		public ExprOperatorNode visitExpressionOperator(BMoThParser.ExpressionOperatorContext ctx) {
			ArrayList<ExprNode> exprNodes = new ArrayList<>();
			List<ExpressionContext> expressionList = ctx.expression();
			for (ExpressionContext expressionContext : expressionList) {
				ExprNode exprNode = (ExprNode) expressionContext.accept(this);
				exprNodes.add(exprNode);
			}
			String operator = ctx.operator.getText();
			return new ExprOperatorNode(ctx, exprNodes, operator);
		}

		@Override
		public ExprNode visitNumberExpression(BMoThParser.NumberExpressionContext ctx) {
			int value = Integer.parseInt(ctx.Number().getText());
			return new NumberNode(ctx, value);
		}

		// Predicates
		@Override
		public PredicateNode visitPredicateOperator(BMoThParser.PredicateOperatorContext ctx) {
			List<PredicateNode> list = new ArrayList<>();
			List<PredicateContext> predicate = ctx.predicate();
			for (PredicateContext predicateContext : predicate) {
				PredicateNode predNode =(PredicateNode) predicateContext.accept(this);
				list.add(predNode);
			}
			return new PredicateOperatorNode(ctx, list);
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
			if (sublist.size() > 1) {
				return sublist.get(0);
			} else {
				return new ParallelSubstitutionNode(sublist);
			}
		}

		private IdentifierExprNode createIdentifierExprNode(Token token) {
			Token declToken = SemanticAstCreator.this.machineAnalyser.declarationReferences.get(token);
			DeclarationNode declarationNode = declarationMap.get(declToken);
			return new IdentifierExprNode(token, declarationNode);
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

	public MachineNode getMachineNode() {
		return this.machineNode;
	}

}
