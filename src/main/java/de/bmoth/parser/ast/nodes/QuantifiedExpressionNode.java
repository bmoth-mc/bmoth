package de.bmoth.parser.ast.nodes;

import java.util.List;

import de.bmoth.antlr.BMoThParser.ExpressionContext;

public class QuantifiedExpressionNode extends ExprNode {

	public static enum QuatifiedExpressionOperator {
		SET_COMPREHENSION
	}

	private final List<DeclarationNode> declarationList;
	private final PredicateNode predicateNode;
	private QuatifiedExpressionOperator operator;

	public QuantifiedExpressionNode(ExpressionContext ctx, List<DeclarationNode> declarationList,
			PredicateNode predNode, QuatifiedExpressionOperator operator) {
		this.declarationList = declarationList;
		this.predicateNode = predNode;
		this.operator = operator;
	}

	public List<DeclarationNode> getDeclarationList() {
		return declarationList;
	}

	public PredicateNode getPredicateNode() {
		return predicateNode;
	}

	public QuatifiedExpressionOperator getOperator() {
		return operator;
	}


}
