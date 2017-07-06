package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertNextPhiUntilPsiToNextPhiUntilNextPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode nextOperator = (LTLPrefixOperatorNode) oldNode;
        if (nextOperator.getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
            LTLNode argument = nextOperator.getArgument();
            if (argument instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode untilOperator = (LTLInfixOperatorNode) argument;
                if (untilOperator.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
                    LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NEXT, untilOperator.getLeft());
                    LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NEXT, untilOperator.getRight());
                    untilOperator.setLeft(newNextLeft);
                    untilOperator.setRight(newNextRight);
                    return untilOperator;
                }
            }
        }
        return oldNode;
    }
}
