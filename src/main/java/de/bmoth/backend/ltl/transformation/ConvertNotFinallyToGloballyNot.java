package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.LTLASTTransformation;

import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.*;

public class ConvertNotFinallyToGloballyNot extends LTLASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, FINALLY);
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode not = (LTLPrefixOperatorNode) node;
        LTLPrefixOperatorNode innerFinally = (LTLPrefixOperatorNode) not.getArgument();
        LTLNode inner = innerFinally.getArgument();

        setChanged();
        return new LTLPrefixOperatorNode(GLOBALLY, new LTLPrefixOperatorNode(NOT, inner));
    }

}
