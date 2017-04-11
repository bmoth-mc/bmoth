package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExprOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgs;

public abstract class AbstractVisitor<R, P> {

	public R visitPredicateNode(PredicateNode node, P expected) {
		if (node instanceof PredicateOperatorNode) {
			return visitPredicateOperatorNode((PredicateOperatorNode) node, expected);
		} else if (node instanceof PredicateOperatorWithExprArgs) {
			return visitPredicateOperatorWithExprArgs((PredicateOperatorWithExprArgs) node, expected);
		}
		throw new AssertionError();
	}

	public abstract R visitPredicateOperatorNode(PredicateOperatorNode node, P expected);

	public abstract R visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgs node, P expected);

	public R visitExprNode(ExprNode node, P expected) {
		if (node instanceof ExprOperatorNode) {
			return visitExprOperatorNode((ExprOperatorNode) node, expected);
		} else if (node instanceof IdentifierExprNode) {
			return visitIdentifierExprNode((IdentifierExprNode) node, expected);
		} else if (node instanceof NumberNode) {
			return visitNumberNode((NumberNode) node, expected);
		}
		throw new AssertionError();
	}

	public abstract R visitExprOperatorNode(ExprOperatorNode node, P expected);

	public abstract R visitIdentifierExprNode(IdentifierExprNode node, P expected);

	public abstract R visitNumberNode(NumberNode node, P expected);
}
