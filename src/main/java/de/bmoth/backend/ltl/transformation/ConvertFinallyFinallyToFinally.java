package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.FINALLY;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertFinallyFinallyToFinally extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, FINALLY) && (contains(node, FINALLY) || contains(node, NOT, FINALLY));
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode outerFinally = (LTLPrefixOperatorNode) node;

        setChanged();

        // case FF->F
        if (contains(outerFinally, FINALLY)) {
            LTLPrefixOperatorNode innerFinally = (LTLPrefixOperatorNode) outerFinally.getArgument();
            LTLNode inner = innerFinally.getArgument();

            return new LTLPrefixOperatorNode(FINALLY, inner);
        }
        // case FnF->nF
        else {
            LTLPrefixOperatorNode not = (LTLPrefixOperatorNode) outerFinally.getArgument();
            LTLPrefixOperatorNode innerFinally = (LTLPrefixOperatorNode) not.getArgument();
            LTLNode inner = innerFinally.getArgument();

            return new LTLPrefixOperatorNode(NOT, new LTLPrefixOperatorNode(FINALLY, inner));
        }
    }
}
