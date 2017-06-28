package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertNotUntil extends AbstractASTTransformation {

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
                LTLInfixOperatorNode untilOperator = (LTLInfixOperatorNode) argument;
                if (untilOperator.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
                    LTLNode untilsLeft = untilOperator.getLeft();
                    LTLNode untilsRight = untilOperator.getRight();
                    LTLPrefixOperatorNode notUntilsRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, untilsRight);
                    LTLInfixOperatorNode untilsLeftAndNotUntilsRight = new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.AND, untilsLeft, notUntilsRight);
                    LTLPrefixOperatorNode notUntilsLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, untilsLeft);
                    LTLInfixOperatorNode notUntilsLeftAndNotUntilsRight = new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.AND, notUntilsLeft, notUntilsRight);
                    setChanged();
                    return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.WEAK_UNTIL, untilsLeftAndNotUntilsRight, notUntilsLeftAndNotUntilsRight);
                }
            }
        }
        return oldNode;
    }

}
