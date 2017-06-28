package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertNotWeakUntil extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) oldNode;
        if (notOperator.getKind() == LTLPrefixOperatorNode.Kind.NOT) {
            LTLNode argument = notOperator.getArgument();
            if (argument instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode weakUntilOperator = (LTLInfixOperatorNode) argument;
                if (weakUntilOperator.getKind() == LTLInfixOperatorNode.Kind.WEAK_UNTIL) {
                    LTLNode weakUntilsLeft = weakUntilOperator.getLeft();
                    LTLNode weakUntilsRight = weakUntilOperator.getRight();
                    LTLPrefixOperatorNode notWeakUntilsRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, weakUntilsRight);
                    LTLInfixOperatorNode weakUntilsLeftAndNotWeakUntilsRight = new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.AND, weakUntilsLeft, notWeakUntilsRight);
                    LTLPrefixOperatorNode notWeakUntilsLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, weakUntilsLeft);
                    LTLInfixOperatorNode notWeakUntilsLeftAndNotWeakUntilsRight = new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.AND, notWeakUntilsLeft, notWeakUntilsRight);
                    setChanged();
                    return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.UNTIL, weakUntilsLeftAndNotWeakUntilsRight, notWeakUntilsLeftAndNotWeakUntilsRight);
                }
            }
        }
        return oldNode;
    }

}
