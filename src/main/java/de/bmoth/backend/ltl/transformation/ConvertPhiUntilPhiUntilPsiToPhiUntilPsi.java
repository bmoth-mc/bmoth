package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.LTLASTTransformation;

import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.UNTIL;

public class ConvertPhiUntilPhiUntilPsiToPhiUntilPsi extends LTLASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, UNTIL) &&
            (containsLeft(node, UNTIL)
                && rightChild(leftChild(node)).toString().equals(
                rightChild(node).toString())

                ||
                containsRight(node, UNTIL)
                    && leftChild(rightChild(node)).toString().equals(
                    leftChild(node).toString())
            );

    }

    @Override
    public Node transformNode(Node node) {
        LTLInfixOperatorNode outerUntil = (LTLInfixOperatorNode) node;
        LTLNode originalLeft = outerUntil.getLeft();
        LTLNode originalRight = outerUntil.getRight();

        setChanged();

        // case U(U(x,y),y)->U(x,y)
        if (isOperator(originalLeft, UNTIL)) {
            LTLInfixOperatorNode innerUntil = (LTLInfixOperatorNode) originalLeft;
            LTLNode newLeft = innerUntil.getLeft();

            return new LTLInfixOperatorNode(UNTIL, newLeft, originalRight);
        }
        // case U(x,U(x,y))->U(x,y)
        else {
            LTLInfixOperatorNode innerUntil = (LTLInfixOperatorNode) originalRight;
            LTLNode newRight = innerUntil.getRight();

            return new LTLInfixOperatorNode(UNTIL, originalLeft, newRight);
        }
    }
}
