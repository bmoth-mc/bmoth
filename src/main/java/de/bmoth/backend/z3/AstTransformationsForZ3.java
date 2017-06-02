package de.bmoth.backend.z3;

import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.bmoth.parser.ast.ASTTransformationVisitor;
import de.bmoth.parser.ast.AbstractASTTransformation;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;

public class AstTransformationsForZ3 {
    private static AstTransformationsForZ3 instance;

    private final List<AbstractASTTransformation> transformationList;

    private AstTransformationsForZ3() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNestedUnionsToUnionList());
        transformationList.add(new ConvertElementOfUnionToMultipleElementOfs());
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
        public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
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
                    return new PredicateOperatorNode( node.getParseTree(), PredicateOperator.OR,
                            predicateArguments);
                }
            }
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

}
