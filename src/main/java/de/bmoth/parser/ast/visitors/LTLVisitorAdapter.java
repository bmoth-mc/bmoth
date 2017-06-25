package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.ltl.*;

public interface LTLVisitorAdapter<R, P> extends AbstractVisitor<R, P> {
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
