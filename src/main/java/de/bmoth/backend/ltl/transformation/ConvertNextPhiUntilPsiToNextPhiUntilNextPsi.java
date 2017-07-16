package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.UNTIL;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NEXT;

public class ConvertNextPhiUntilPsiToNextPhiUntilNextPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode nextOperator = (LTLPrefixOperatorNode) oldNode;
        if (nextOperator.getKind() == NEXT) {
            LTLNode argument = nextOperator.getArgument();
            if (argument instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode untilOperator = (LTLInfixOperatorNode) argument;
                if (untilOperator.getKind() == UNTIL) {
                    LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(NEXT, untilOperator.getLeft());
                    LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(NEXT, untilOperator.getRight());
                    return new LTLInfixOperatorNode(UNTIL, newNextLeft, newNextRight);
                }
            }
        }
        return oldNode;
    }
}
