package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.*;

public abstract class AbstractVisitor<R, P> {

    public R visitPredicateNode(PredicateNode node, P expected) {
        if (node instanceof PredicateOperatorNode) {
            return visitPredicateOperatorNode((PredicateOperatorNode) node, expected);
        } else if (node instanceof PredicateOperatorWithExprArgsNode) {
            return visitPredicateOperatorWithExprArgs((PredicateOperatorWithExprArgsNode) node, expected);
        } else if (node instanceof IdentifierPredicateNode) {
            return visitIdentifierPredicateNode((IdentifierPredicateNode) node, expected);
        } else if (node instanceof QuantifiedPredicateNode) {
            return visitQuantifiedPredicateNode((QuantifiedPredicateNode) node, expected);
        }
        throw new AssertionError(node);
    }

    public R visitExprNode(ExprNode node, P expected) {
        if (node instanceof ExpressionOperatorNode) {
            return visitExprOperatorNode((ExpressionOperatorNode) node, expected);
        } else if (node instanceof IdentifierExprNode) {
            return visitIdentifierExprNode((IdentifierExprNode) node, expected);
        } else if (node instanceof NumberNode) {
            return visitNumberNode((NumberNode) node, expected);
        } else if (node instanceof QuantifiedExpressionNode) {
            return visitQuantifiedExpressionNode((QuantifiedExpressionNode) node, expected);
        } else if (node instanceof CastPredicateExpressionNode) {
            return visitCastPredicateExpressionNode((CastPredicateExpressionNode) node, expected);
        }
        throw new AssertionError();
    }

    public R visitSubstitutionNode(SubstitutionNode node, P expected) {
        if (node instanceof SelectSubstitutionNode) {
            return visitSelectSubstitutionNode((SelectSubstitutionNode) node, expected);
        } else if (node instanceof SingleAssignSubstitutionNode) {
            return visitSingleAssignSubstitution((SingleAssignSubstitutionNode) node, expected);
        } else if (node instanceof ParallelSubstitutionNode) {
            return visitParallelSubstitutionNode((ParallelSubstitutionNode) node, expected);
        } else if (node instanceof AnySubstitutionNode) {
            return visitAnySubstitution((AnySubstitutionNode) node, expected);
        }
        throw new AssertionError();
    }

    public abstract R visitPredicateOperatorNode(PredicateOperatorNode node, P expected);

    public abstract R visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, P expected);

    public abstract R visitExprOperatorNode(ExpressionOperatorNode node, P expected);

    public abstract R visitIdentifierExprNode(IdentifierExprNode node, P expected);

    public abstract R visitCastPredicateExpressionNode(CastPredicateExpressionNode node, P expected);

    public abstract R visitNumberNode(NumberNode node, P expected);

    public abstract R visitSelectSubstitutionNode(SelectSubstitutionNode node, P expected);

    public abstract R visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, P expected);

    public abstract R visitAnySubstitution(AnySubstitutionNode node, P expected);

    public abstract R visitParallelSubstitutionNode(ParallelSubstitutionNode node, P expected);

    public abstract R visitIdentifierPredicateNode(IdentifierPredicateNode node, P expected);

    public abstract R visitQuantifiedExpressionNode(QuantifiedExpressionNode node, P expected);

    public abstract R visitQuantifiedPredicateNode(QuantifiedPredicateNode node, P expected);
}
