package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesElementOfSubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesSuchThatSubstitutionNode;
import de.bmoth.parser.ast.nodes.ConditionSubstitutionNode;
import de.bmoth.parser.ast.nodes.IfSubstitutionNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;
import de.bmoth.parser.ast.nodes.SkipSubstitutionNode;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;

public interface FormulaVisitor<R, P> extends AbstractVisitor<R, P> {

    @Override
    default R visitSelectSubstitutionNode(SelectSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitIfSubstitutionNode(IfSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitParallelSubstitutionNode(ParallelSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitAnySubstitution(AnySubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitConditionSubstitutionNode(ConditionSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitSkipSubstitutionNode(SkipSubstitutionNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitLTLNode(LTLNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitLTLKeywordNode(LTLKeywordNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitLTLInfixOperatorNode(LTLInfixOperatorNode node, P expected) {
        throw new AssertionError();
    }

    @Override
    default R visitLTLBPredicateNode(LTLBPredicateNode node, P expected) {
        throw new AssertionError();
    }

}
