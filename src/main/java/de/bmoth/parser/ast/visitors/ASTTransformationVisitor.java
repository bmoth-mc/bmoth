package de.bmoth.parser.ast.visitors;

import java.util.List;
import java.util.stream.Collectors;

import de.bmoth.parser.ast.nodes.AbstractIfAndSelectSubstitutionsNode;
import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesElementOfSubstitutionNode;
import de.bmoth.parser.ast.nodes.BecomesSuchThatSubstitutionNode;
import de.bmoth.parser.ast.nodes.CastPredicateExpressionNode;
import de.bmoth.parser.ast.nodes.ConditionSubstitutionNode;
import de.bmoth.parser.ast.nodes.DeferredSetNode;
import de.bmoth.parser.ast.nodes.EnumeratedSetElementNode;
import de.bmoth.parser.ast.nodes.EnumerationSetNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.IfSubstitutionNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;
import de.bmoth.parser.ast.nodes.SkipSubstitutionNode;
import de.bmoth.parser.ast.nodes.SubstitutionNode;

public class ASTTransformationVisitor {

    private final List<AbstractASTTransformation> modifierList;

    public ASTTransformationVisitor(List<AbstractASTTransformation> modifierList) {
        this.modifierList = modifierList;
    }

    public PredicateNode transformPredicate(PredicateNode node) {
        ASTVisitor astVisitor = new ASTVisitor();
        return (PredicateNode) astVisitor.visitPredicateNode(node, null);
    }

    public ExprNode transformExpr(ExprNode node) {
        ASTVisitor astVisitor = new ASTVisitor();
        return (ExprNode) astVisitor.visitExprNode(node, null);
    }

    private class ASTVisitor implements AbstractVisitor<Node, Void> {

        private Node modifyNode(Node node, Void expected) {
            Node temp = node;
            boolean run = true;
            while (run) {
                run = false;
                for (AbstractASTTransformation astModifier : modifierList) {
                    temp = astModifier.visitNode(temp, expected);
                    if (astModifier.hasChanged()) {
                        run = true;
                        astModifier.resetChanged();
                    }
                }
            }
            return temp;
        }

        @Override
        public Node visitPredicateOperatorNode(PredicateOperatorNode node, Void expected) {
            List<PredicateNode> list = node.getPredicateArguments().stream()
                    .map(predNode -> (PredicateNode) visitPredicateNode(predNode, expected))
                    .collect(Collectors.toList());
            node.setPredicateList(list);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
            final List<ExprNode> argumentList = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            node.setArgumentsList(argumentList);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
            final List<ExprNode> arguments = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            node.setExpressionList(arguments);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
            Node arg = visitPredicateNode(node.getPredicate(), expected);
            node.setArg((PredicateNode) arg);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitNumberNode(NumberNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
            return visitConditionsAndSubstitutionsNode(node, expected);
        }

        @Override
        public Node visitIfSubstitutionNode(IfSubstitutionNode node, Void expected) {
            return visitConditionsAndSubstitutionsNode(node, expected);
        }

        private Node visitConditionsAndSubstitutionsNode(AbstractIfAndSelectSubstitutionsNode node, Void expected) {
            node.setConditions(node.getConditions().stream().map(t -> (PredicateNode) visitPredicateNode(t, expected))
                    .collect(Collectors.toList()));
            node.setSubstitutions(node.getSubstitutions().stream()
                    .map(t -> (SubstitutionNode) visitSubstitutionNode(t, expected)).collect(Collectors.toList()));
            if (null != node.getElseSubstitution()) {
                SubstitutionNode elseSub = (SubstitutionNode) visitSubstitutionNode(node.getElseSubstitution(),
                        expected);
                node.setElseSubstitution(elseSub);
            }
            return modifyNode(node, expected);
        }

        @Override
        public Node visitConditionSubstitutionNode(ConditionSubstitutionNode node, Void expected) {
            node.setCondition((PredicateNode) visitPredicateNode(node.getCondition(), expected));
            node.setSubstitution((SubstitutionNode) visitSubstitutionNode(node.getSubstitution(), expected));
            return modifyNode(node, expected);
        }

        @Override
        public Node visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void expected) {
            node.setValue((ExprNode) visitExprNode(node.getValue(), expected));
            return modifyNode(node, expected);
        }

        @Override
        public Node visitAnySubstitution(AnySubstitutionNode node, Void expected) {
            node.setPredicate((PredicateNode) visitPredicateNode(node.getWherePredicate(), expected));
            node.setSubstitution((SubstitutionNode) visitSubstitutionNode(node.getThenSubstitution(), expected));
            return modifyNode(node, expected);
        }

        @Override
        public Node visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
            List<SubstitutionNode> substitutions = node.getSubstitutions().stream()
                    .map(sub -> (SubstitutionNode) visitSubstitutionNode(node, expected)).collect(Collectors.toList());
            node.setSubstitutions(substitutions);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
            PredicateNode visitPredicateNode = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
            node.setPredicate(visitPredicateNode);
            if (node.getExpressionNode() != null) {
                ExprNode expr = (ExprNode) visitExprNode(node.getExpressionNode(), expected);
                node.setExpr(expr);
            }
            return modifyNode(node, expected);
        }

        @Override
        public Node visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
            PredicateNode pred = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
            node.setPredicate(pred);
            return modifyNode(node, expected);
        }

        @Override
        public Node visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Void expected) {
            node.setPredicate((PredicateNode) visitPredicateNode(node.getPredicate(), expected));
            return modifyNode(node, expected);
        }

        @Override
        public Node visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Void expected) {
            node.setExpression((ExprNode) visitExprNode(node.getExpression(), expected));
            return modifyNode(node, expected);
        }

        @Override
        public Node visitSkipSubstitutionNode(SkipSubstitutionNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitEnumerationSetNode(EnumerationSetNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitDeferredSetNode(DeferredSetNode node, Void expected) {
            return modifyNode(node, expected);
        }

        @Override
        public Node visitEnumeratedSetElementNode(EnumeratedSetElementNode node, Void expected) {
            return modifyNode(node, expected);
        }

    }
}