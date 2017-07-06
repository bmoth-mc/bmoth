package de.bmoth.backend.ltl.transformation;


import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;

public class ConvertWeakToRelease implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, LTLInfixOperatorNode.Kind.WEAK_UNTIL);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLInfixOperatorNode weakOperator = (LTLInfixOperatorNode) oldNode;
        LTLNode innerLeft = weakOperator.getLeft();
        LTLNode innerRight = weakOperator.getRight();
        return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.RELEASE, innerRight, new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.OR, innerLeft, innerRight));
    }
}
