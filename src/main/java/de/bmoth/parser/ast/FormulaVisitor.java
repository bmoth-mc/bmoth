package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesElementOfSubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesSuchThatSubstitutionNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;

public interface FormulaVisitor<R, P> extends AbstractVisitor<R, P> {

    @Override
    default R visitSelectSubstitutionNode(SelectSubstitutionNode node, P expected) {
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
}
