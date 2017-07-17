package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.FINALLY;

public class ConvertFinallyPhiOrPsiToFinallyPhiOrFinallyPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, FINALLY) && contains(node, OR);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) oldNode;
        LTLNode argument = finallyOperator.getArgument();
        LTLInfixOperatorNode orOperator = (LTLInfixOperatorNode) argument;
        LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(FINALLY, orOperator.getLeft());
        LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(FINALLY, orOperator.getRight());
        return new LTLInfixOperatorNode(OR, newNextLeft, newNextRight);
    }
}
