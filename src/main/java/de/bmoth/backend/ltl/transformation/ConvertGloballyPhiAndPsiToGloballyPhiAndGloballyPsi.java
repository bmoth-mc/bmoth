package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertGloballyPhiAndPsiToGloballyPhiAndGloballyPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) oldNode;
        if (globallyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
            LTLNode argument = globallyOperator.getArgument();
            if (argument instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode andOperator = (LTLInfixOperatorNode) argument;
                if (andOperator.getKind() == LTLInfixOperatorNode.Kind.AND) {
                    LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.GLOBALLY, andOperator.getLeft());
                    LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.GLOBALLY, andOperator.getRight());
                    andOperator.setLeft(newNextLeft);
                    andOperator.setRight(newNextRight);
                    return andOperator;
                }
            }
        }
        return oldNode;
    }
}
