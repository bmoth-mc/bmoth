package de.bmoth.parser.ast;

import java.util.ArrayList;
import java.util.List;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.CastPredicateExpressionNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;

public class AstTransformationForZ3 extends AbstractVisitor<Node, Void> {

    public static PredicateNode transformSemanticNode(PredicateNode node) {
        AstTransformationForZ3 astTransformerForZ3 = new AstTransformationForZ3();
        return (PredicateNode) astTransformerForZ3.visitPredicateNode(node, null);
    }

    public static ExprNode transformExprNode(ExprNode value) {
        AstTransformationForZ3 astTransformerForZ3 = new AstTransformationForZ3();
        return (ExprNode) astTransformerForZ3.visitExprNode(value, null);
    }

    @Override
    public Node visitPredicateOperatorNode(PredicateOperatorNode node, Void expected) {
        List<PredicateNode> list = new ArrayList<>();
        for (PredicateNode pred : node.getPredicateArguments()) {
            Node p = visitPredicateNode(pred, expected);
            list.add((PredicateNode) p);
        }
        node.setPredicateList(list);
        return node;
    }

    @Override
    public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
        List<ExprNode> argumentList = new ArrayList<>();
        for (ExprNode expr : node.getExpressionNodes()) {
            Node e = visitExprNode(expr, expected);
            argumentList.add((ExprNode) e);
        }
        switch (node.getOperator()) {
        case ELEMENT_OF: {
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
                return new PredicateOperatorNode(PredicateOperator.OR, predicateArguments);
            }
        }
        default:
            break;
        }
        node.setArgumentsList(argumentList);
        return node;
    }

    @Override
    public Node visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
        List<ExprNode> arguments = new ArrayList<>();
        for (ExprNode expr : node.getExpressionNodes()) {
            ExprNode e = (ExprNode) visitExprNode(expr, expected);
            arguments.add(e);
        }
        switch (node.getOperator()) {
        case UNION: {
            List<ExprNode> list = new ArrayList<>();
            for (ExprNode expr : node.getExpressionNodes()) {
                if (expr instanceof ExpressionOperatorNode
                        && ((ExpressionOperatorNode) expr).getOperator() == ExpressionOperator.UNION) {
                    list.addAll(((ExpressionOperatorNode) expr).getExpressionNodes());
                } else {
                    list.add(expr);
                }
                node.setExpressionList(list);
            }
            return node;
        }
        default:
            break;
        }
        node.setExpressionList(arguments);
        return node;
    }

    @Override
    public Node visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
        Node arg = visitPredicateNode(node.getPredicate(), expected);
        node.setArg((PredicateNode) arg);
        return node;
    }

    @Override
    public Node visitNumberNode(NumberNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
        throw new AssertionError();
    }

    @Override
    public Node visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void expected) {
        throw new AssertionError();
    }

    @Override
    public Node visitAnySubstitution(AnySubstitutionNode node, Void expected) {
        throw new AssertionError();
    }

    @Override
    public Node visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
        throw new AssertionError();
    }

    @Override
    public Node visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
        return node;
    }

    @Override
    public Node visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
        PredicateNode visitPredicateNode = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
        node.setPredicate(visitPredicateNode);
        if (node.getExpressionNode() != null) {
            ExprNode expr = (ExprNode) visitExprNode(node.getExpressionNode(), expected);
            node.setExpr(expr);
        }
        return node;
    }

    @Override
    public Node visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
        PredicateNode pred = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
        node.setPredicate(pred);
        return node;
    }

}
