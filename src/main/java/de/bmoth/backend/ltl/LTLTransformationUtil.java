package de.bmoth.backend.ltl;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;

public class LTLTransformationUtil {
    public static boolean isOperator(Node node, LTLPrefixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && isOperator((LTLPrefixOperatorNode) node, operator);
    }

    public static boolean isOperator(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && isOperator((LTLInfixOperatorNode) node, operator);
    }

    public static boolean isOperator(Node node, LTLKeywordNode.Kind operator) {
        return node instanceof LTLKeywordNode && isOperator((LTLKeywordNode) node, operator);
    }

    public static boolean isOperator(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return node.getKind() == operator;
    }

    public static boolean isOperator(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator) {
        return node.getKind() == operator;
    }

    public static boolean isOperator(LTLKeywordNode node, LTLKeywordNode.Kind operator) {
        return node.getKind() == operator;
    }

    public static boolean contains(Node node, LTLPrefixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator);
    }

    public static boolean contains(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator) {
        return isOperator(node.getArgument(), operator);
    }

    public static boolean contains(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator);
    }

    public static boolean contains(LTLPrefixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return isOperator(node.getArgument(), operator);
    }

    public static boolean contains(LTLPrefixOperatorNode node, LTLKeywordNode.Kind operator) {
        return isOperator(node.getArgument(), operator);
    }

    public static boolean contains(Node node, LTLKeywordNode.Kind operator) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator);
    }

    public static boolean contains(Node node, LTLPrefixOperatorNode.Kind operator1, LTLPrefixOperatorNode.Kind operator2) {
        return node instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) node, operator1, operator2);
    }

    public static boolean contains(LTLPrefixOperatorNode node, LTLPrefixOperatorNode.Kind operator1, LTLPrefixOperatorNode.Kind operator2) {
        LTLNode inner = node.getArgument();
        return contains(node, operator1) && inner instanceof LTLPrefixOperatorNode && contains((LTLPrefixOperatorNode) inner, operator2);
    }

    public static boolean containsLeft(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsLeft((LTLInfixOperatorNode) node, operator);
    }

    public static boolean containsLeft(Node node, LTLKeywordNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsLeft((LTLInfixOperatorNode) node, operator);
    }

    public static boolean containsLeft(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return isOperator(node.getLeft(), operator);
    }

    public static boolean containsLeft(LTLInfixOperatorNode node, LTLKeywordNode.Kind operator) {
        return isOperator(node.getLeft(), operator);
    }

    public static boolean containsRight(Node node, LTLInfixOperatorNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsRight((LTLInfixOperatorNode) node, operator);
    }

    public static boolean containsRight(Node node, LTLKeywordNode.Kind operator) {
        return node instanceof LTLInfixOperatorNode && containsRight((LTLInfixOperatorNode) node, operator);
    }

    public static boolean containsRight(LTLInfixOperatorNode node, LTLInfixOperatorNode.Kind operator) {
        return isOperator(node.getRight(), operator);
    }

    public static boolean containsRight(LTLInfixOperatorNode node, LTLKeywordNode.Kind operator) {
        return isOperator(node.getRight(), operator);
    }

    public static LTLNode leftChild(Node node) {
        return node instanceof LTLInfixOperatorNode ? leftChild((LTLInfixOperatorNode) node) : null;
    }

    public static LTLNode leftChild(LTLInfixOperatorNode node) {
        return node.getLeft();
    }

    public static LTLNode rightChild(Node node) {
        return node instanceof LTLInfixOperatorNode ? rightChild((LTLInfixOperatorNode) node) : null;
    }

    public static LTLNode rightChild(LTLInfixOperatorNode node) {
        return node.getRight();
    }
}
