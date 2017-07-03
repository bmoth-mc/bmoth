package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.TRUE;

public class RemoveTrueOr implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, OR)
            && (containsLeft(node, TRUE)
            || containsRight(node, TRUE));
    }

    @Override
    public Node transformNode(Node node) {
        return new LTLKeywordNode(TRUE);
    }
}
