package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;


public abstract class LTLASTTransformation extends AbstractASTTransformation {

    protected boolean isOperator(Node node, LTLPrefixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && isOperator((LTLPrefixOperatorNode) node, operator);
    }

    protected boolean isOperator(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator) {
        return node.getKind() == operator;
    }

    protected boolean contains(Node node, LTLPrefixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator);
    }

    protected boolean contains(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator) {
        return isOperator(node.getArgument(), operator);
    }

    protected boolean contains(Node node, LTLPrefixOperatorNode.Kind operator1, LTLPrefixOperatorNode.Kind operator2) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator1, operator2);
    }

    protected boolean contains(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator1, LTLPrefixOperatorNode.Kind operator2) {
        LTLNode inner = node.getArgument();
        return contains(node, operator1) && inner instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) inner, operator2);
    }
}
