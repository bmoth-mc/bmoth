package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.AND;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;

public class RemoveFalseAnd extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, AND) && (containsLeft(node, FALSE) || containsRight(node, FALSE));
    }

    @Override
    public Node transformNode(Node node) {
        return new LTLKeywordNode(FALSE);
    }
}
