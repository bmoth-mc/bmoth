package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesElementOfSubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesSuchThatSubstitutionNode;
import de.bmoth.parser.ast.nodes.CastPredicateExpressionNode;
import de.bmoth.parser.ast.nodes.ConditionSubstitutionNode;
import de.bmoth.parser.ast.nodes.DeferredSetNode;
import de.bmoth.parser.ast.nodes.EnumeratedSetElementNode;
import de.bmoth.parser.ast.nodes.EnumerationSetNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.IfSubstitutionNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;
import de.bmoth.parser.ast.nodes.SkipSubstitutionNode;
import de.bmoth.parser.ast.nodes.SubstitutionNode;

public class AbstractASTTransformation implements AbstractVisitor<Node, Void> {

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    protected void setChanged() {
        changed = true;
    }

    public void resetChanged() {
        this.changed = false;
    }

    public Node visitNode(Node node, Void expected) {
        if (node instanceof ExprNode) {
            return visitExprNode((ExprNode) node, expected);
        } else if (node instanceof PredicateNode) {
            return visitPredicateNode((PredicateNode) node, expected);
        } else if (node instanceof SubstitutionNode) {
            return visitSubstitutionNode((SubstitutionNode) node, expected);
        }
        throw new AssertionError();
    }

    // returning node means do nothing

    @Override
    public Node visitPredicateOperatorNode(PredicateOperatorNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitNumberNode(NumberNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitAnySubstitution(AnySubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitConditionSubstitutionNode(ConditionSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitIfSubstitutionNode(IfSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitSkipSubstitutionNode(SkipSubstitutionNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitEnumerationSetNode(EnumerationSetNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitDeferredSetNode(DeferredSetNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitEnumeratedSetElementNode(EnumeratedSetElementNode node, Void expected) {
        return node;
    }

}
