package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NEXT;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNotNextToNextNot implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, NEXT);
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) node;
        LTLNode argument = notOperator.getArgument();
        LTLPrefixOperatorNode nextOperator = (LTLPrefixOperatorNode) argument;
        LTLPrefixOperatorNode newNot = new LTLPrefixOperatorNode(NOT, nextOperator.getArgument());
        return new LTLPrefixOperatorNode(NEXT, newNot);
    }
}
