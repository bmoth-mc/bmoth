package de.bmoth.backend.z3.transformation;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.Arrays;

public class ConvertMemberOfIntervalToLeqAndGeq extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof PredicateOperatorWithExprArgsNode;
    }

    @Override
    public Node transformNode(Node node2) {
        PredicateOperatorWithExprArgsNode node = (PredicateOperatorWithExprArgsNode) node2;
        if (node.getOperator() == PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.ELEMENT_OF) {
            ExprNode left = node.getExpressionNodes().get(0);
            ExprNode right = node.getExpressionNodes().get(1);
            if (right instanceof ExpressionOperatorNode
                && ((ExpressionOperatorNode) right).getOperator() == ExpressionOperatorNode.ExpressionOperator.INTERVAL) {
                ExpressionOperatorNode interval = (ExpressionOperatorNode) right;

                ExprNode leftBound = interval.getExpressionNodes().get(0);
                ExprNode rightBound = interval.getExpressionNodes().get(1);
                PredicateNode geqLeftBound = new PredicateOperatorWithExprArgsNode(leftBound.getParseTree(),
                    PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.GREATER_EQUAL, Arrays.asList(left, leftBound));
                PredicateNode leqRightBound = new PredicateOperatorWithExprArgsNode(leftBound.getParseTree(),
                    PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.LESS_EQUAL, Arrays.asList(left, rightBound));

                setChanged();
                return new PredicateOperatorNode(node.getParseTree(), PredicateOperatorNode.PredicateOperator.AND,
                    Arrays.asList(geqLeftBound, leqRightBound));
            }
        }
        return node;
    }
}
