package de.bmoth.backend.z3.transformation;

import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.math.BigInteger;

public class ConstantFolding extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof ExpressionOperatorNode;
    }

    @Override
    public Node transformNode(Node inputNode) {
        ExpressionOperatorNode node = (ExpressionOperatorNode) inputNode;
        ExprNode left = node.getExpressionNodes().get(0);
        ExprNode right = node.getExpressionNodes().get(1);
        if (right instanceof NumberNode && left instanceof NumberNode) {
            BigInteger leftValue = ((NumberNode) left).getValue();
            BigInteger rightValue = ((NumberNode) right).getValue();
            setChanged();
            switch (node.getOperator()) {
                case PLUS:
                    return new NumberNode(node.getParseTree(), leftValue.add(rightValue));
                case MINUS:
                    return new NumberNode(node.getParseTree(), leftValue.subtract(rightValue));
                case MULT:
                    return new NumberNode(node.getParseTree(), leftValue.multiply(rightValue));
                case DIVIDE:
                    return new NumberNode(node.getParseTree(), leftValue.divide(rightValue));
                default:
                    resetChanged();
                    return node;
            }
        }
        return node;
    }
}
