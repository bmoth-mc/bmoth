package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;

public interface AbstractVisitor<R, P> {

    /*
     * Expressions
     */

    default R visitExprNode(ExprNode node, P expected) {
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
        } else if (node instanceof EnumeratedSetElementNode) {
            return visitEnumeratedSetElementNode((EnumeratedSetElementNode) node, expected);
        } else if (node instanceof DeferredSetNode) {
            return visitDeferredSetNode((DeferredSetNode) node, expected);
        } else if (node instanceof EnumerationSetNode) {
            return visitEnumerationSetNode((EnumerationSetNode) node, expected);
        }
        throw new AssertionError();
    }

    R visitEnumerationSetNode(EnumerationSetNode node, P expected);

    R visitDeferredSetNode(DeferredSetNode node, P expected);

    R visitEnumeratedSetElementNode(EnumeratedSetElementNode node, P expected);

    R visitExprOperatorNode(ExpressionOperatorNode node, P expected);

    R visitIdentifierExprNode(IdentifierExprNode node, P expected);

    R visitCastPredicateExpressionNode(CastPredicateExpressionNode node, P expected);

    R visitNumberNode(NumberNode node, P expected);

    R visitQuantifiedExpressionNode(QuantifiedExpressionNode node, P expected);

    /*
     * Predicates
     */

    default R visitPredicateNode(PredicateNode node, P expected) {
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

    R visitIdentifierPredicateNode(IdentifierPredicateNode node, P expected);

    R visitPredicateOperatorNode(PredicateOperatorNode node, P expected);

    R visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, P expected);

    R visitQuantifiedPredicateNode(QuantifiedPredicateNode node, P expected);

    /*
     * Substitutions
     */

    default R visitSubstitutionNode(SubstitutionNode node, P expected) {
        if (node instanceof SelectSubstitutionNode) {
            return visitSelectSubstitutionNode((SelectSubstitutionNode) node, expected);
        } else if (node instanceof SingleAssignSubstitutionNode) {
            return visitSingleAssignSubstitution((SingleAssignSubstitutionNode) node, expected);
        } else if (node instanceof ParallelSubstitutionNode) {
            return visitParallelSubstitutionNode((ParallelSubstitutionNode) node, expected);
        } else if (node instanceof AnySubstitutionNode) {
            return visitAnySubstitution((AnySubstitutionNode) node, expected);
        } else if (node instanceof BecomesSuchThatSubstitutionNode) {
            return visitBecomesSuchThatSubstitutionNode((BecomesSuchThatSubstitutionNode) node, expected);
        } else if (node instanceof BecomesElementOfSubstitutionNode) {
            return visitBecomesElementOfSubstitutionNode((BecomesElementOfSubstitutionNode) node, expected);
        } else if (node instanceof ConditionSubstitutionNode) {
            return visitConditionSubstitutionNode((ConditionSubstitutionNode) node, expected);
        } else if (node instanceof IfSubstitutionNode) {
            return visitIfSubstitutionNode((IfSubstitutionNode) node, expected);
        } else if (node instanceof SkipSubstitutionNode) {
            return visitSkipSubstitutionNode((SkipSubstitutionNode) node, expected);
        }
        throw new AssertionError(node.getClass());
    }

    R visitSkipSubstitutionNode(SkipSubstitutionNode node, P expected);

    R visitIfSubstitutionNode(IfSubstitutionNode node, P expected);

    R visitConditionSubstitutionNode(ConditionSubstitutionNode node, P expected);

    R visitAnySubstitution(AnySubstitutionNode node, P expected);

    R visitSelectSubstitutionNode(SelectSubstitutionNode node, P expected);

    R visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, P expected);

    R visitParallelSubstitutionNode(ParallelSubstitutionNode node, P expected);

    R visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, P expected);

    R visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, P expected);

    /*
     * Substitutions
     */

    default R visitLTLNode(LTLNode node, P expected) {
        if (node instanceof LTLBPredicateNode) {
            return visitLTLBPredicateNode((LTLBPredicateNode) node, expected);
        } else if (node instanceof LTLInfixOperatorNode) {
            return visitLTLInfixOperatorNode((LTLInfixOperatorNode) node, expected);
        } else if (node instanceof LTLKeywordNode) {
            return visitLTLKeywordNode((LTLKeywordNode) node, expected);
        } else if (node instanceof LTLPrefixOperatorNode) {
            return visitLTLPrefixOperatorNode((LTLPrefixOperatorNode) node, expected);
        }
        throw new AssertionError(node.getClass());
    }

    R visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, P expected);

    R visitLTLKeywordNode(LTLKeywordNode node, P expected);

    R visitLTLInfixOperatorNode(LTLInfixOperatorNode node, P expected);

    R visitLTLBPredicateNode(LTLBPredicateNode node, P expected);

}
