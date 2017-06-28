package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;


public abstract class LTLASTTransformation extends AbstractASTTransformation {

    protected boolean isOperator(Node node, LTLPrefixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && isOperator((LTLPrefixOperatorNode) node, operator);
    }

    protected boolean isOperator(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && isOperator((LTLInfixOperatorNode) node, operator);
    }

    protected boolean isOperator(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return node.getKind() == operator;
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

    protected boolean containsLeft(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsLeft((LTLInfixOperatorNode) node, operator);
    }

    protected boolean containsLeft(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return isOperator(node.getLeft(), operator);
    }

    protected boolean containsRight(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsRight((LTLInfixOperatorNode) node, operator);
    }

    protected boolean containsRight(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return isOperator(node.getRight(), operator);
    }

    protected LTLNode leftChild(Node node) {
        return node instanceof LTLInfixOperatorNode ? leftChild((LTLInfixOperatorNode) node) : null;
    }

    protected LTLNode leftChild(LTLInfixOperatorNode node) {
        return node.getLeft();
    }

    protected LTLNode rightChild(Node node) {
        return node instanceof LTLInfixOperatorNode ? rightChild((LTLInfixOperatorNode) node) : null;
    }

    protected LTLNode rightChild(LTLInfixOperatorNode node) {
        return node.getRight();
    }
}
