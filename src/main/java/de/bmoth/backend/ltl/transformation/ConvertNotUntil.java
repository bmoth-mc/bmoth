package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertNotUntil implements ASTTransformation {

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
                    LTLNode left = untilOperator.getLeft();
                    LTLNode right = untilOperator.getRight();
                    LTLPrefixOperatorNode notRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, right);
                    LTLPrefixOperatorNode notLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, left);
                    return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.RELEASE, notLeft, notRight);
                }
            }
        }
        return oldNode;
    }

}
