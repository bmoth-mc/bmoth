package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.TRUE;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNegatedLTLLiterals extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && (contains(node, FALSE) || contains(node, TRUE));
    }

    @Override
    public Node transformNode(Node node) {
        setChanged();

        LTLPrefixOperatorNode notNode = (LTLPrefixOperatorNode) node;

        if (contains(notNode, FALSE)) {
            return new LTLKeywordNode(TRUE);
        } else {
            return new LTLKeywordNode(FALSE);
        }
    }
}
