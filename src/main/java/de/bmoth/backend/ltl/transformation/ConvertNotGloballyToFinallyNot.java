package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.*;

public class ConvertNotGloballyToFinallyNot extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, GLOBALLY);
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode not = (LTLPrefixOperatorNode) node;
        LTLPrefixOperatorNode globally = (LTLPrefixOperatorNode) not.getArgument();
        LTLNode inner = globally.getArgument();

        return new LTLPrefixOperatorNode(FINALLY, new LTLPrefixOperatorNode(NOT, inner));
    }
}
