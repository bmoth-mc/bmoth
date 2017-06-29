package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.*;
import org.junit.Before;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.AND;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.TRUE;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.GLOBALLY;

public class FormulaAndSubstitutionVisitorExceptionsTest {
    private FormulaAndSubstitutionVisitor<Object, Object> visitor;

    @Test(expected = AssertionError.class)
    public void testDoNotHandleLTLNode() {
        LTLNode node = new LTLBPredicateNode(null);
        visitor.visitLTLNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void testDoNotHandleHandleLTLBPredicateNode() {
        LTLBPredicateNode node = new LTLBPredicateNode(null);
        visitor.visitLTLBPredicateNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void testDoNotHandleLTLInfixOperatorNode() {
        LTLInfixOperatorNode node = new LTLInfixOperatorNode(AND, null, null);
        visitor.visitLTLInfixOperatorNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void testDoNotHandleLTLKeywordNode() {
        LTLKeywordNode node = new LTLKeywordNode(TRUE);
        visitor.visitLTLKeywordNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void testDoNotHandleLTLPrefixOperatorNode() {
        LTLPrefixOperatorNode node = new LTLPrefixOperatorNode(GLOBALLY, null);
        visitor.visitLTLPrefixOperatorNode(node, null);
    }

    @Before
    public void init() {
        visitor = new FormulaAndSubstitutionVisitor<Object, Object>() {
            @Override
            public Object visitEnumerationSetNode(EnumerationSetNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitDeferredSetNode(DeferredSetNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitEnumeratedSetElementNode(EnumeratedSetElementNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitExprOperatorNode(ExpressionOperatorNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitIdentifierExprNode(IdentifierExprNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitNumberNode(NumberNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitIdentifierPredicateNode(IdentifierPredicateNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitPredicateOperatorNode(PredicateOperatorNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitSkipSubstitutionNode(SkipSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitIfSubstitutionNode(IfSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitConditionSubstitutionNode(ConditionSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitAnySubstitution(AnySubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitSelectSubstitutionNode(SelectSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitParallelSubstitutionNode(ParallelSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Object expected) {
                return null;
            }
        };
    }
}
