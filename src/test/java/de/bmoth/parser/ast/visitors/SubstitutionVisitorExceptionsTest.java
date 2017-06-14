package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

public class SubstitutionVisitorExceptionsTest {
    private TerminalNode terminalNode;
    private SubstitutionVisitor<Object, Object> visitor;

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleExpr() {
        ExprNode node = new ExpressionOperatorNode(null, null, ExpressionOperatorNode.ExpressionOperator.BOOL);
        visitor.visitExprNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleExprOperator() {
        ExpressionOperatorNode node = new ExpressionOperatorNode(null, null,
                ExpressionOperatorNode.ExpressionOperator.BOOL);
        visitor.visitExprOperatorNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleIdentifierExpr() {
        IdentifierExprNode node = new IdentifierExprNode(terminalNode, null);
        visitor.visitIdentifierExprNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleCastPredicateExpr() {
        CastPredicateExpressionNode node = new CastPredicateExpressionNode(null, null);
        visitor.visitCastPredicateExpressionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleNumber() {
        NumberNode node = new NumberNode(null, null);
        visitor.visitNumberNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleQuantifiedExpr() {
        QuantifiedExpressionNode node = new QuantifiedExpressionNode(null, null, null, null,
                QuantifiedExpressionNode.QuantifiedExpressionOperator.SET_COMPREHENSION);
        visitor.visitQuantifiedExpressionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandlePredicate() {
        PredicateNode node = new PredicateOperatorNode(null, PredicateOperatorNode.PredicateOperator.AND,
                Collections.emptyList());
        visitor.visitPredicateNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandlePredicateOperator() {
        PredicateOperatorNode node = new PredicateOperatorNode(null, PredicateOperatorNode.PredicateOperator.AND, null);
        visitor.visitPredicateOperatorNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleIdentifierPredicate() {
        IdentifierPredicateNode node = new IdentifierPredicateNode(terminalNode, null);
        visitor.visitIdentifierPredicateNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandlePredicateOperatorWithExprArgs() {
        PredicateOperatorWithExprArgsNode node = new PredicateOperatorWithExprArgsNode(null,
                PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.ELEMENT_OF, null);
        visitor.visitPredicateOperatorWithExprArgs(node, null);
    }

    @Test(expected = AssertionError.class)
    @Ignore("nullpointer exception, how to construct quantified predicate note properly?")
    public void substitutionVisitorsDoNotHandleQuantifiedPredicate() {
        QuantifiedPredicateNode node = new QuantifiedPredicateNode(null, null, null);
        visitor.visitQuantifiedPredicateNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleEnumerationSet() {
        EnumerationSetNode node = new EnumerationSetNode(null, null, null);
        visitor.visitEnumerationSetNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleDeferredSet() {
        DeferredSetNode node = new DeferredSetNode(null, null, null);
        visitor.visitDeferredSetNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void substitutionVisitorsDoNotHandleEnumeratedSetElement() {
        EnumeratedSetElementNode node = new EnumeratedSetElementNode(null, null, null, null);
        visitor.visitEnumeratedSetElementNode(node, null);
    }

    @Before
    public void init() {
        visitor = new SubstitutionVisitor<Object, Object>() {
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
            public Object visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node,
                    Object expected) {
                return null;
            }

            @Override
            public Object visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitLTLKeywordNode(LTLKeywordNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitLTLInfixOperatorNode(LTLInfixOperatorNode node, Object expected) {
                return null;
            }

            @Override
            public Object visitLTLBPredicateNode(LTLBPredicateNode node, Object expected) {
                return null;
            }
        };
        terminalNode = new TerminalNode() {
            @Override
            public Token getSymbol() {
                return new Token() {
                    @Override
                    public String getText() {
                        return "adsf";
                    }

                    @Override
                    public int getType() {
                        return 0;
                    }

                    @Override
                    public int getLine() {
                        return 0;
                    }

                    @Override
                    public int getCharPositionInLine() {
                        return 0;
                    }

                    @Override
                    public int getChannel() {
                        return 0;
                    }

                    @Override
                    public int getTokenIndex() {
                        return 0;
                    }

                    @Override
                    public int getStartIndex() {
                        return 0;
                    }

                    @Override
                    public int getStopIndex() {
                        return 0;
                    }

                    @Override
                    public TokenSource getTokenSource() {
                        return null;
                    }

                    @Override
                    public CharStream getInputStream() {
                        return null;
                    }
                };
            }

            @Override
            public ParseTree getParent() {
                return null;
            }

            @Override
            public ParseTree getChild(int i) {
                return null;
            }

            @Override
            public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
                return null;
            }

            @Override
            public String getText() {
                return null;
            }

            @Override
            public String toStringTree(Parser parser) {
                return null;
            }

            @Override
            public Interval getSourceInterval() {
                return null;
            }

            @Override
            public Object getPayload() {
                return null;
            }

            @Override
            public int getChildCount() {
                return 0;
            }

            @Override
            public String toStringTree() {
                return null;
            }

            @Override
            public void setParent(RuleContext parent) {
                // not needed in test stub
            }
        };
    }
}
