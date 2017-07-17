package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.AND;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.GLOBALLY;

public class ConvertGloballyPhiAndPsiToGloballyPhiAndGloballyPsi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, GLOBALLY) && contains(node, AND);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) oldNode;
        LTLNode argument = globallyOperator.getArgument();
        LTLInfixOperatorNode andOperator = (LTLInfixOperatorNode) argument;
        LTLPrefixOperatorNode newNextLeft = new LTLPrefixOperatorNode(GLOBALLY, andOperator.getLeft());
        LTLPrefixOperatorNode newNextRight = new LTLPrefixOperatorNode(GLOBALLY, andOperator.getRight());
        return new LTLInfixOperatorNode(AND, newNextLeft, newNextRight);
    }
}
