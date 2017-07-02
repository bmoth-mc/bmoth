package de.bmoth.backend.z3.transformation;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvertElementOfIntersectionToMultipleElementOfs extends AbstractASTTransformation {

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
                && ((ExpressionOperatorNode) right).getOperator() == ExpressionOperatorNode.ExpressionOperator.INTERSECTION) {
                List<PredicateNode> predicateArguments = new ArrayList<>();
                ExpressionOperatorNode union = (ExpressionOperatorNode) right;
                for (ExprNode set : union.getExpressionNodes()) {
                    PredicateOperatorWithExprArgsNode predicateOperatorWithExprArgsNode = new PredicateOperatorWithExprArgsNode(
                        set.getParseTree(), PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.ELEMENT_OF, Arrays.asList(left, set));
                    predicateArguments.add(predicateOperatorWithExprArgsNode);
                }
                return new PredicateOperatorNode(node.getParseTree(), PredicateOperatorNode.PredicateOperator.AND, predicateArguments);
            }
        }
        return node;
    }
}
