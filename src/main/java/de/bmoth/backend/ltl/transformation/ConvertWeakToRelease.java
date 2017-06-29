package de.bmoth.backend.ltl.transformation;


import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;

public class ConvertWeakToRelease extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, LTLInfixOperatorNode.Kind.WEAK_UNTIL);
    }


    @Override
    public Node transformNode(Node oldNode) {
        LTLInfixOperatorNode weakOperator = (LTLInfixOperatorNode) oldNode;
        if (weakOperator.getKind() == LTLInfixOperatorNode.Kind.WEAK_UNTIL) {
            LTLNode weaksLeft = weakOperator.getLeft();
            LTLNode weaksRight = weakOperator.getRight();
            LTLNode leftOrRight = new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.OR, weaksLeft, weaksRight);
            setChanged();
            return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.RELEASE, weaksRight, leftOrRight);

        }

        return oldNode;
    }


}
