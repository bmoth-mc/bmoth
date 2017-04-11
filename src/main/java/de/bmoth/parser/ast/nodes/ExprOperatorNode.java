package de.bmoth.parser.ast.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionOperatorContext;

public class ExprOperatorNode extends ExprNode {

	public static enum ExpressionOperator {
		PLUS, MINUS// ...
	}

	private static final Map<Integer, ExpressionOperator> map = new HashMap<>();
	static {
		map.put(BMoThParser.PLUS, ExpressionOperator.PLUS);
	}



	private final List<ExprNode> expressionNodes;
	private final String operatorString;
	private final int arity;
	private ExpressionOperator expressionOperator;

	public ExprOperatorNode(ExpressionOperatorContext ctx, List<ExprNode> expressionNodes, String operatorString) {
		this.arity = expressionNodes.size();
		this.expressionNodes = expressionNodes;
		this.operatorString = operatorString;
	}

	
	
	public ExpressionOperator getExpressionOperator() {
		return expressionOperator;
	}

	public void setExpressionOperator(ExpressionOperator expressionOperator) {
		this.expressionOperator = expressionOperator;
	}

	public List<ExprNode> getExpressionNodes() {
		return expressionNodes;
	}
}
