package de.bmoth.backend.z3.transformation;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;
import de.bmoth.preferences.BMothPreferences;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import static de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;

public class ConvertMemberOfIntegerSetToLeqGeq extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof PredicateOperatorWithExprArgsNode;
    }

    @Override
    public Node transformNode(Node node2) {
        PredicateOperatorWithExprArgsNode node = (PredicateOperatorWithExprArgsNode) node2;
        if (node.getOperator() == PredOperatorExprArgs.ELEMENT_OF) {
            ExprNode left = node.getExpressionNodes().get(0);
            ExprNode right = node.getExpressionNodes().get(1);
            if (right instanceof ExpressionOperatorNode) {
                ExpressionOperatorNode castedRight = (ExpressionOperatorNode) right;
                switch (castedRight.getOperator()) {
                    case NATURAL:
                        return new PredicateOperatorWithExprArgsNode(node.getParseTree(),
                            PredOperatorExprArgs.GREATER_EQUAL, Arrays.asList(left, new NumberNode(right.getParseTree(), BigInteger.ZERO)));
                    case NATURAL1:
                        return new PredicateOperatorWithExprArgsNode(node.getParseTree(),
                            PredOperatorExprArgs.GREATER_EQUAL, Arrays.asList(left, new NumberNode(right.getParseTree(), BigInteger.ONE)));
                    case INTEGER:
                        return new PredicateOperatorNode(node.getParseTree(), PredicateOperator.TRUE, Collections.emptyList());
                    case INT:
                        return upperAndLowerBoundsConstraint(node, left, right, BigInteger.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
                    case NAT:
                        return upperAndLowerBoundsConstraint(node, left, right, BigInteger.ZERO);
                    default:
                        break;
                }
            }
        }
        return node;
    }

    private Node upperAndLowerBoundsConstraint(PredicateOperatorWithExprArgsNode node, ExprNode left, ExprNode right, BigInteger lowerBound) {
        PredicateOperatorWithExprArgsNode geq = new PredicateOperatorWithExprArgsNode(node.getParseTree(),
            PredOperatorExprArgs.GREATER_EQUAL, Arrays.asList(left, new NumberNode(right.getParseTree(), lowerBound)));
        PredicateOperatorWithExprArgsNode leq = new PredicateOperatorWithExprArgsNode(node.getParseTree(),
            PredOperatorExprArgs.LESS_EQUAL, Arrays.asList(left, new NumberNode(right.getParseTree(), BigInteger.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)))));
        return new PredicateOperatorNode(node.getParseTree(), PredicateOperator.AND, Arrays.asList(leq, geq));
    }
}
