package de.bmoth.parser.ast.nodes;

import java.util.List;

import de.bmoth.antlr.BMoThParser.PredicateOperatorWithExprArgsContext;

public class PredicateOperatorWithExprArgs extends PredicateNode {

	public static enum PredOperatorExprArgs {
		EQUAL, UNEQUAL
	}
	
	private final List<ExprNode> expressionNodes;
	private PredOperatorExprArgs operator;
	
	public PredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsContext ctx,
			List<ExprNode> expressionNodes) {
		this.expressionNodes = expressionNodes;
	}

	public PredOperatorExprArgs getOperator() {
		return operator;
	}

	public void setOperator(PredOperatorExprArgs operator) {
		this.operator = operator;
	}

	public List<ExprNode> getExpressionNodes() {
		return expressionNodes;
	}

}
