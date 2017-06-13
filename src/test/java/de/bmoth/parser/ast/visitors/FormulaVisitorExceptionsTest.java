package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.*;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class FormulaVisitorExceptionsTest {
    private SkipSubstitutionNode skip = new SkipSubstitutionNode();
    private TerminalNode terminalNode;
    private FormulaVisitor<Object, Object> visitor;

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleSelectSubstitution() {
        SubstitutionNode node = new SelectSubstitutionNode(null, Collections.emptyList(), null);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleIfSubstitution() {
        SubstitutionNode node = new IfSubstitutionNode(Collections.emptyList(), Collections.emptyList(), null);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleSingleAssignSubstitution() {
        SubstitutionNode node = new SingleAssignSubstitutionNode(new IdentifierExprNode(terminalNode, null), null);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleParallelSubstitution() {
        SubstitutionNode node = new ParallelSubstitutionNode(Collections.emptyList());
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleBecomesElementOfSubstitution() {
        SubstitutionNode node = new BecomesElementOfSubstitutionNode(Collections.emptyList(), null);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleBecomesSuchThatSubstitution() {
        SubstitutionNode node = new BecomesSuchThatSubstitutionNode(Collections.emptyList(), null);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleAnySubstitution() {
        SubstitutionNode node = new AnySubstitutionNode(Collections.emptyList(), null, skip);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleConditionSubstitution() {
        SubstitutionNode node = new ConditionSubstitutionNode(null, null, skip);
        visitor.visitSubstitutionNode(node, null);
    }

    @Test(expected = AssertionError.class)
    public void formulaVisitorsDoNotHandleSkipSubstitution() {
        SubstitutionNode node = new SkipSubstitutionNode();
        visitor.visitSubstitutionNode(node, null);
    }

    @Before
    public void init() {
        visitor = new FormulaVisitor<Object, Object>() {
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
        };
        terminalNode = new TerminalNode() {
            @Override
            public Token getSymbol() {
                return null;
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
