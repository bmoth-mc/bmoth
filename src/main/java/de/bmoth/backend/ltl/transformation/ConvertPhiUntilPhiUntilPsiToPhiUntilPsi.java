package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.UNTIL;

public class ConvertPhiUntilPhiUntilPsiToPhiUntilPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, UNTIL) &&
            (containsLeft(node, UNTIL) && rightChild(leftChild(node)).equalAst(rightChild(node)) || containsRight(node, UNTIL) && leftChild(rightChild(node)).equalAst(leftChild(node)));

    }

    @Override
    public Node transformNode(Node node) {
        LTLInfixOperatorNode outerUntil = (LTLInfixOperatorNode) node;
        // case U(U(x,y),y)->U(x,y)
        if (containsLeft(node, UNTIL)) {
            return outerUntil.getLeft();
        }
        // case U(x,U(x,y))->U(x,y)
        else {
            return outerUntil.getRight();
        }
    }
}
