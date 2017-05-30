package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;

public class AstTransformationForZ3 {
    private static AstTransformationForZ3 instance;

    private final List<AbstractASTTransformation> transformationList;

    private AstTransformationForZ3() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNestedUnionsToUnionList());
        transformationList.add(new ConvertElementOfUnionToMultipleElementOfs());
    }

    public static AstTransformationForZ3 getInstance() {
        if (null == instance) {
            instance = new AstTransformationForZ3();
        }
        return instance;
    }

    private PredicateNode transformPredicate(PredicateNode predNode) {
        PredicateNode temp = predNode;
        for (AbstractASTTransformation abstractASTTransformation : transformationList) {
            temp = (PredicateNode) abstractASTTransformation.visitPredicateNode(temp, null);
        }
        return temp;
    }

    private ExprNode transformExpresssion(ExprNode node) {
        ExprNode temp = node;
        for (AbstractASTTransformation abstractASTTransformation : transformationList) {
            temp = (ExprNode) abstractASTTransformation.visitExprNode(temp, null);
        }
        return node;
    }

    public static PredicateNode transformSemanticNode(PredicateNode node) {
        AstTransformationForZ3 astTransformerForZ3 = new AstTransformationForZ3();
        return astTransformerForZ3.transformPredicate(node);

    }

    public static ExprNode transformExprNode(ExprNode value) {
        AstTransformationForZ3 astTransformerForZ3 = new AstTransformationForZ3();
        return astTransformerForZ3.transformExpresssion(value);
    }

    private class ConvertElementOfUnionToMultipleElementOfs extends AbstractASTTransformation {

        @Override
        public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
            final List<ExprNode> argumentList = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            if (node.getOperator() == PredOperatorExprArgs.ELEMENT_OF) {
                ExprNode left = argumentList.get(0);
                ExprNode right = argumentList.get(1);
                if (right instanceof ExpressionOperatorNode
                        && ((ExpressionOperatorNode) right).getOperator() == ExpressionOperator.UNION) {
                    List<PredicateNode> predicateArguments = new ArrayList<>();
                    ExpressionOperatorNode union = (ExpressionOperatorNode) right;
                    for (ExprNode set : union.getExpressionNodes()) {
                        List<ExprNode> args = new ArrayList<>();
                        args.add(left);
                        args.add(set);
                        PredicateOperatorWithExprArgsNode predicateOperatorWithExprArgsNode = new PredicateOperatorWithExprArgsNode(
                                PredOperatorExprArgs.ELEMENT_OF, args);
                        predicateArguments.add(predicateOperatorWithExprArgsNode);
                    }
                    super.setChanged();
                    return new PredicateOperatorNode(PredicateOperator.OR, predicateArguments);
                }
            }
            node.setArgumentsList(argumentList);
            return node;
        }
    }

    private class ConvertNestedUnionsToUnionList extends AbstractASTTransformation {
        @Override
        public Node visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
            final List<ExprNode> arguments = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            if (node.getOperator() == ExpressionOperator.UNION) {
                List<ExprNode> list = new ArrayList<>();
                for (ExprNode expr : node.getExpressionNodes()) {
                    if (expr instanceof ExpressionOperatorNode
                            && ((ExpressionOperatorNode) expr).getOperator() == ExpressionOperator.UNION) {
                        list.addAll(((ExpressionOperatorNode) expr).getExpressionNodes());
                        super.setChanged();
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

}
