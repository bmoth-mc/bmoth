package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.UNTIL;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNotUntil implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, UNTIL);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) oldNode;
        LTLNode argument = notOperator.getArgument();
        LTLInfixOperatorNode untilOperator = (LTLInfixOperatorNode) argument;
        LTLNode left = untilOperator.getLeft();
        LTLNode right = untilOperator.getRight();
        LTLPrefixOperatorNode notRight = new LTLPrefixOperatorNode(NOT, right);
        LTLPrefixOperatorNode notLeft = new LTLPrefixOperatorNode(NOT, left);
        return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.RELEASE, notLeft, notRight);
    }
}
