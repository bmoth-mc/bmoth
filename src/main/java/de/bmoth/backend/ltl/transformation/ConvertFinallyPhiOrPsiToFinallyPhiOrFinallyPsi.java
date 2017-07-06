package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertFinallyPhiOrPsiToFinallyPhiOrFinallyPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) oldNode;
        if (finallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
            LTLNode argument = finallyOperator.getArgument();
            if (argument instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode orOperator = (LTLInfixOperatorNode) argument;
                if (orOperator.getKind() == LTLInfixOperatorNode.Kind.OR) {
                    LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.FINALLY, orOperator.getLeft());
                    LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.FINALLY, orOperator.getRight());
                    orOperator.setLeft(newNextLeft);
                    orOperator.setRight(newNextRight);
                    return orOperator;
                }
            }
        }
        return oldNode;
    }
}
