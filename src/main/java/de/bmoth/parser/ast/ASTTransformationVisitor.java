package de.bmoth.parser.ast;

import java.util.List;
import java.util.stream.Collectors;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.CastPredicateExpressionNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
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

        private PredicateNode modifyPredicateNode(PredicateNode node, Void expected) {
            PredicateNode temp = node;
            boolean run = true;
            while (run) {
                run = false;
                for (AbstractASTTransformation astModifier : modifierList) {
                    temp = (PredicateNode) astModifier.visitPredicateNode(temp, expected);
                    if (astModifier.hasChanged()) {
                        run = true;
                        astModifier.resetChanged();
                    }
                }
            }
            return temp;
        }

        private ExprNode modifyExprNode(ExprNode node, Void expected) {
            ExprNode temp = node;
            boolean run = true;
            while (run) {
                run = false;
                for (AbstractASTTransformation astModifier : modifierList) {
                    temp = (ExprNode) astModifier.visitExprNode(temp, expected);
                    if (astModifier.hasChanged()) {
                        run = true;
                        astModifier.resetChanged();
                    }
                }
            }
            return temp;
        }

        private SubstitutionNode modifySubstitutionNode(SubstitutionNode node, Void expected) {
            SubstitutionNode temp = node;
            boolean run = true;
            while (run) {
                run = false;
                for (AbstractASTTransformation astModifier : modifierList) {
                    temp = (SubstitutionNode) astModifier.visitSubstitutionNode(temp, expected);
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
            return modifyPredicateNode(node, expected);
        }

        @Override
        public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
            final List<ExprNode> argumentList = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            node.setArgumentsList(argumentList);
            return modifyPredicateNode(node, expected);
        }

        @Override
        public Node visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
            final List<ExprNode> arguments = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) visitExprNode(exprNode, expected)).collect(Collectors.toList());
            node.setExpressionList(arguments);
            return modifyExprNode(node, expected);
        }

        @Override
        public Node visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
            return modifyExprNode(node, expected);
        }

        @Override
        public Node visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
            Node arg = visitPredicateNode(node.getPredicate(), expected);
            node.setArg((PredicateNode) arg);
            return modifyExprNode(node, expected);
        }

        @Override
        public Node visitNumberNode(NumberNode node, Void expected) {
            return modifyExprNode(node, expected);
        }

        @Override
        public Node visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
            node.setCondition((PredicateNode) visitPredicateNode(node.getCondition(), expected));
            node.setSubstitution((SubstitutionNode) visitSubstitutionNode(node.getSubstitution(), expected));
            return modifySubstitutionNode(node, expected);
        }

        @Override
        public Node visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void expected) {
            node.setValue((ExprNode) visitExprNode(node.getValue(), expected));
            return modifySubstitutionNode(node, expected);
        }

        @Override
        public Node visitAnySubstitution(AnySubstitutionNode node, Void expected) {
            node.setPredicate((PredicateNode) visitPredicateNode(node.getWherePredicate(), expected));
            node.setSubstitution((SubstitutionNode) visitSubstitutionNode(node.getThenSubstitution(), expected));
            return modifySubstitutionNode(node, expected);
        }

        @Override
        public Node visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
            List<SubstitutionNode> substitutions = node.getSubstitutions().stream()
                    .map(sub -> (SubstitutionNode) visitSubstitutionNode(node, expected)).collect(Collectors.toList());
            node.setSubstitutions(substitutions);
            return modifySubstitutionNode(node, expected);
        }

        @Override
        public Node visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
            return modifyPredicateNode(node, expected);
        }

        @Override
        public Node visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
            PredicateNode visitPredicateNode = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
            node.setPredicate(visitPredicateNode);
            if (node.getExpressionNode() != null) {
                ExprNode expr = (ExprNode) visitExprNode(node.getExpressionNode(), expected);
                node.setExpr(expr);
            }
            return modifyExprNode(node, expected);
        }

        @Override
        public Node visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
            PredicateNode pred = (PredicateNode) visitPredicateNode(node.getPredicateNode(), expected);
            node.setPredicate(pred);
            return modifyPredicateNode(node, expected);
        }
    }
}
