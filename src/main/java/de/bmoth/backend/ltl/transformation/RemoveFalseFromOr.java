package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;

public class RemoveFalseFromOr extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, OR) && (containsLeft(node, FALSE) || containsRight(node, FALSE));
    }

    @Override
    public Node transformNode(Node node) {
        LTLInfixOperatorNode andNode = (LTLInfixOperatorNode) node;

        if (isOperator(andNode.getLeft(), FALSE)) {
            return andNode.getRight();
        } else {
            return andNode.getLeft();
        }
    }
}
