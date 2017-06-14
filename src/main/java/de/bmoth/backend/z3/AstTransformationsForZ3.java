package de.bmoth.backend.z3;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstTransformationsForZ3 {
    private static AstTransformationsForZ3 instance;

    private final List<AbstractASTTransformation> transformationList;

    private AstTransformationsForZ3() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNestedUnionsToUnionList());
        transformationList.add(new ConvertElementOfUnionToMultipleElementOfs());
        transformationList.add(new ConvertMemberOfIntervalToLeqAndGeq());
    }

    public static AstTransformationsForZ3 getInstance() {
        if (null == instance) {
            instance = new AstTransformationsForZ3();
        }
        return instance;
    }

    public static PredicateNode transformPredicate(PredicateNode predNode) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformPredicate(predNode);
    }

    public static ExprNode transformExprNode(ExprNode value) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformExpr(value);
    }

    private class ConvertElementOfUnionToMultipleElementOfs extends AbstractASTTransformation {

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
                if (right instanceof ExpressionOperatorNode
                        && ((ExpressionOperatorNode) right).getOperator() == ExpressionOperator.UNION) {
                    List<PredicateNode> predicateArguments = new ArrayList<>();
                    ExpressionOperatorNode union = (ExpressionOperatorNode) right;
                    for (ExprNode set : union.getExpressionNodes()) {
                        List<ExprNode> args = new ArrayList<>();
                        args.add(left);
                        args.add(set);
                        PredicateOperatorWithExprArgsNode predicateOperatorWithExprArgsNode = new PredicateOperatorWithExprArgsNode(
                                set.getParseTree(), PredOperatorExprArgs.ELEMENT_OF, args);
                        predicateArguments.add(predicateOperatorWithExprArgsNode);
                    }
                    setChanged();
                    return new PredicateOperatorNode(node.getParseTree(), PredicateOperator.OR, predicateArguments);
                }
            }
            return node;
        }
    }

    private class ConvertNestedUnionsToUnionList extends AbstractASTTransformation {

        @Override
        public boolean canHandleNode(Node node) {
            return node instanceof ExpressionOperatorNode;
        }

        @Override
        public Node transformNode(Node node2) {
            ExpressionOperatorNode node = (ExpressionOperatorNode) node2;
            final List<ExprNode> arguments = node.getExpressionNodes();
            if (node.getOperator() == ExpressionOperator.UNION) {
                List<ExprNode> list = new ArrayList<>();
                for (ExprNode expr : node.getExpressionNodes()) {
                    if (expr instanceof ExpressionOperatorNode
                            && ((ExpressionOperatorNode) expr).getOperator() == ExpressionOperator.UNION) {
                        list.addAll(((ExpressionOperatorNode) expr).getExpressionNodes());
                        setChanged();
                    } else {
                        list.add(expr);
                    }
                }
                node.setExpressionList(list);
                return node;
            } else {
                node.setExpressionList(arguments);
                return node;
            }
        }
    }

    private class ConvertMemberOfIntervalToLeqAndGeq extends AbstractASTTransformation {

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
                if (right instanceof ExpressionOperatorNode
                        && ((ExpressionOperatorNode) right).getOperator() == ExpressionOperator.INTERVAL) {
                    ExpressionOperatorNode interval = (ExpressionOperatorNode) right;

                    ExprNode leftBound = interval.getExpressionNodes().get(0);
                    ExprNode rightBound = interval.getExpressionNodes().get(1);
                    PredicateNode geqLeftBound = new PredicateOperatorWithExprArgsNode(leftBound.getParseTree(),
                            PredOperatorExprArgs.GREATER_EQUAL, Arrays.asList(left, leftBound));
                    PredicateNode leqRightBound = new PredicateOperatorWithExprArgsNode(leftBound.getParseTree(),
                            PredOperatorExprArgs.LESS_EQUAL, Arrays.asList(left, rightBound));

                    setChanged();
                    return new PredicateOperatorNode(node.getParseTree(), PredicateOperator.AND,
                            Arrays.asList(geqLeftBound, leqRightBound));
                }
            }
            return node;
        }
    }

}
