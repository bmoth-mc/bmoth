package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.TRUE;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNegatedLTLLiterals implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && (contains(node, FALSE) || contains(node, TRUE));
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode notNode = (LTLPrefixOperatorNode) node;

        if (contains(notNode, FALSE)) {
            return new LTLKeywordNode(TRUE);
        } else {
            return new LTLKeywordNode(FALSE);
        }
    }
}
