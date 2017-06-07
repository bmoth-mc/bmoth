package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.CastPredicateExpressionNode;
import de.bmoth.parser.ast.nodes.DeferredSetNode;
import de.bmoth.parser.ast.nodes.EnumeratedSetElementNode;
import de.bmoth.parser.ast.nodes.EnumerationSetNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;

public interface SubstitutionVisitor<R, P> extends AbstractVisitor<R, P> {

    /*
     * Expressions
     */
    @Override
    default R visitExprNode(ExprNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitExprOperatorNode(ExpressionOperatorNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitIdentifierExprNode(IdentifierExprNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitCastPredicateExpressionNode(CastPredicateExpressionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitNumberNode(NumberNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitQuantifiedExpressionNode(QuantifiedExpressionNode node, P expected) {
        throw new AssertionError();
    }

    /*
     * Predicates
     */
    @Override
    default R visitPredicateNode(PredicateNode node, P expected) {
        throw new AssertionError(node);
    }

    @Override
    default R visitIdentifierPredicateNode(IdentifierPredicateNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitPredicateOperatorNode(PredicateOperatorNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitQuantifiedPredicateNode(QuantifiedPredicateNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitEnumerationSetNode(EnumerationSetNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitDeferredSetNode(DeferredSetNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitEnumeratedSetElementNode(EnumeratedSetElementNode node, P expected) {
        throw new AssertionError();
    }

}
