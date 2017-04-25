package de.bmoth.parser.ast.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import de.bmoth.antlr.BMoThParser.ExpressionOperatorContext;

public class ExpressionOperatorNode extends TypedNode implements ExprNode {

	public static enum ExpressionOperator {
		PLUS, MINUS, NATURAL, NATURAL1, INTEGER, BOOL, TRUE, FALSE, POWER_OF //
		, MULT, DIVIDE, MOD, SET_SUBTRACTION, INTERVAL, UNION, SET_ENUMERATION, INTERSECTION, COUPLE, DOMAIN, RANGE
	}

	private static final Map<Integer, ExpressionOperator> map = new HashMap<>();
	static {
		map.put(BMoThParser.PLUS, ExpressionOperator.PLUS);
		map.put(BMoThParser.MINUS, ExpressionOperator.MINUS);
		map.put(BMoThParser.NATURAL, ExpressionOperator.NATURAL);
		map.put(BMoThParser.NATURAL1, ExpressionOperator.NATURAL1);
		map.put(BMoThParser.INTEGER, ExpressionOperator.INTEGER);
		map.put(BMoThParser.BOOL, ExpressionOperator.BOOL);
		map.put(BMoThParser.TRUE, ExpressionOperator.TRUE);
		map.put(BMoThParser.FALSE, ExpressionOperator.FALSE);
		map.put(BMoThParser.POWER_OF, ExpressionOperator.POWER_OF);
		map.put(BMoThParser.MULT, ExpressionOperator.MULT);
		map.put(BMoThParser.DIVIDE, ExpressionOperator.DIVIDE);
		map.put(BMoThParser.MOD, ExpressionOperator.MOD);
		map.put(BMoThParser.SET_SUBTRACTION, ExpressionOperator.SET_SUBTRACTION);
		map.put(BMoThParser.INTERVAL, ExpressionOperator.INTERVAL);
		map.put(BMoThParser.UNION, ExpressionOperator.UNION);
		map.put(BMoThParser.INTERSECTION, ExpressionOperator.INTERSECTION);
		map.put(BMoThParser.MAPLET, ExpressionOperator.COUPLE);
		map.put(BMoThParser.DOM, ExpressionOperator.DOMAIN);
		map.put(BMoThParser.RAN, ExpressionOperator.RANGE);
		
		
	}

	private final List<ExprNode> expressionNodes;
	private final String operatorString;
	private final int arity;
	private ExpressionOperator operator;

	public ExpressionOperatorNode(ExpressionOperatorContext ctx, List<ExprNode> expressionNodes,
			String operatorString) {
		this.arity = expressionNodes.size();
		this.expressionNodes = expressionNodes;
		this.operatorString = operatorString;
		this.operator = loopUpOperator(ctx.operator.getType());
	}

	public ExpressionOperatorNode(ExpressionContext ctx, List<ExprNode> expressionNodes, ExpressionOperator operator) {
		// used for set enumeration, e.g. {1,2,3}
		this.arity = expressionNodes.size();
		this.expressionNodes = expressionNodes;
		this.operatorString = null;
		this.operator = operator;
	}

	private ExpressionOperator loopUpOperator(int type) {
		if (map.containsKey(type)) {
			return map.get(type);
		}
		throw new AssertionError("Operator not implemented: " + operatorString);
	}

	public ExpressionOperator getOperator() {
		return operator;
	}

	public List<ExprNode> getExpressionNodes() {
		return expressionNodes;
	}

	public int getArity() {
		return arity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.operator.name());
		Iterator<ExprNode> iter = expressionNodes.iterator();
		if (iter.hasNext()) {
			sb.append("(");
			while (iter.hasNext()) {
				sb.append(iter.next().toString());
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
