package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.GLOBALLY;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertGloballyGloballyToGlobally implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, GLOBALLY) && (contains(node, GLOBALLY) || contains(node, NOT, GLOBALLY));
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode outerGlobally = (LTLPrefixOperatorNode) node;

        // case GG->G
        if (contains(outerGlobally, GLOBALLY)) {
            LTLPrefixOperatorNode innerGlobally = (LTLPrefixOperatorNode) outerGlobally.getArgument();
            LTLNode inner = innerGlobally.getArgument();


            return new LTLPrefixOperatorNode(GLOBALLY, inner);
        }
        // case GnG->nG
        else {
            LTLPrefixOperatorNode not = (LTLPrefixOperatorNode) outerGlobally.getArgument();
            LTLPrefixOperatorNode innerGlobally = (LTLPrefixOperatorNode) not.getArgument();
            LTLNode inner = innerGlobally.getArgument();

            return new LTLPrefixOperatorNode(NOT, new LTLPrefixOperatorNode(GLOBALLY, inner));
        }
    }
}
