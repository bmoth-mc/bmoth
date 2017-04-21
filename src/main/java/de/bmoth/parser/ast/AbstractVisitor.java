package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitution;
import de.bmoth.parser.ast.nodes.SubstitutionNode;

public abstract class AbstractVisitor<R, P> {

	public R visitPredicateNode(PredicateNode node, P expected) {
		if (node instanceof PredicateOperatorNode) {
			return visitPredicateOperatorNode((PredicateOperatorNode) node, expected);
		} else if (node instanceof PredicateOperatorWithExprArgsNode) {
			return visitPredicateOperatorWithExprArgs((PredicateOperatorWithExprArgsNode) node, expected);
		}
		throw new AssertionError(node);
	}

	public R visitPredicateOperatorNode(PredicateOperatorNode node, P expected) {
		return null;
	}

	public R visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, P expected) {
		return null;
	}

	public R visitExprNode(ExprNode node, P expected) {
		if (node instanceof ExpressionOperatorNode) {
			return visitExprOperatorNode((ExpressionOperatorNode) node, expected);
		} else if (node instanceof IdentifierExprNode) {
			return visitIdentifierExprNode((IdentifierExprNode) node, expected);
		} else if (node instanceof NumberNode) {
			return visitNumberNode((NumberNode) node, expected);
		}
		throw new AssertionError();
	}

	public R visitExprOperatorNode(ExpressionOperatorNode node, P expected) {
		return null;
	}

	public R visitIdentifierExprNode(IdentifierExprNode node, P expected) {
		return null;
	}

	public R visitNumberNode(NumberNode node, P expected) {
		return null;
	}

	public R visitSubstitutionNode(SubstitutionNode node, P expected) {
		if (node instanceof SelectSubstitutionNode) {
			return visitSelectSubstitutionNode((SelectSubstitutionNode) node, expected);
		} else if (node instanceof SingleAssignSubstitution) {
			return visitSingleAssignSubstitution((SingleAssignSubstitution) node, expected);
		} else if (node instanceof ParallelSubstitutionNode) {
			return visitParallelSubstitutionNode((ParallelSubstitutionNode) node, expected);
		}
		throw new AssertionError();
	}

	public R visitSelectSubstitutionNode(SelectSubstitutionNode node, P expected) {
		return null;
	}

	public R visitSingleAssignSubstitution(SingleAssignSubstitution node, P expected) {
		return null;
	}

	public R visitParallelSubstitutionNode(ParallelSubstitutionNode node, P expected) {
		return null;
	}
}
