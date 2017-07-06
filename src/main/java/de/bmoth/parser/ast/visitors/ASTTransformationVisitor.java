package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.TypeChecker;
import de.bmoth.parser.ast.TypeErrorException;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.*;

import java.util.List;
import java.util.stream.Collectors;

public class ASTTransformationVisitor {

    private final List<ASTTransformation> modifierList;

    public ASTTransformationVisitor(List<ASTTransformation> modifierList) {
        this.modifierList = modifierList;
    }

    public void transformLTLFormula(LTLFormula ltlFormula) {
        ASTVisitor astVisitor = new ASTVisitor();
        ltlFormula.setFormula((LTLNode) astVisitor.startTransformation(ltlFormula.getLTLNode()));
        ltlFormula.getLTLNode();
        try {
            TypeChecker.typecheckLTLFormulaNode(ltlFormula);
        } catch (TypeErrorException e) {
            // a type error should only occur when the AST transformation is
            // invalid
            throw new AssertionError(e);
        }
    }

    public LTLNode transformLTLNode(LTLNode ltlNode) {
        ASTVisitor astVisitor = new ASTVisitor();
        return (LTLNode) astVisitor.startTransformation(ltlNode);
    }

    public void transformMachine(MachineNode machineNode) {
        ASTVisitor astVisitor = new ASTVisitor();
        if (machineNode.getProperties() != null) {
            machineNode.setProperties((PredicateNode) astVisitor.startTransformation(machineNode.getProperties()));
        }
        if (machineNode.getInvariant() != null) {
            machineNode.setInvariant((PredicateNode) astVisitor.startTransformation(machineNode.getInvariant()));
        }
        if (machineNode.getInitialisation() != null) {
            machineNode.setInitialisation((SubstitutionNode) astVisitor.startTransformation(machineNode.getInitialisation()));
        }
        machineNode.getOperations()
                .forEach(op -> op.setSubstitution((SubstitutionNode) astVisitor.startTransformation(op.getSubstitution())));

        try {
            TypeChecker.typecheckMachineNode(machineNode);
        } catch (TypeErrorException e) {
            // a type error should only occur when the AST transformation is
            // invalid
            throw new AssertionError(e);
        }
    }

    public void transformFormula(FormulaNode formulaNode) {
        ASTVisitor astVisitor = new ASTVisitor();
        Node visitPredicateNode = astVisitor.startTransformation((PredicateNode) formulaNode.getFormula());
        formulaNode.setFormula(visitPredicateNode);
        try {
            TypeChecker.typecheckFormulaNode(formulaNode);
        } catch (TypeErrorException e) {
            // a type error should only occur when the AST transformation is
            // invalid
            throw new AssertionError(e);
        }
    }

    private class ASTVisitor implements AbstractVisitor<Node, Void> {
        boolean hasChanged = false;

        Node startTransformation(Node node) {
            hasChanged = true;
            Node temp = node;
            while (hasChanged) {
                hasChanged = false;
                temp = modifyNode(temp);
            }
            return temp;
        }

        private Node modifyNode(Node node) {
            for (ASTTransformation astModifier : modifierList) {
                if (astModifier.canHandleNode(node)) {
                    Node temp = astModifier.transformNode(node);
                    if (!temp.equalAst(node)) {
                        // requires a new run on the complete tree
                        hasChanged = true;
                        // revisit the changed node first
                        return modifyNode(temp);
                    }
                }
            }
            return visitNode(node, null);
        }

        @Override
        public Node visitPredicateOperatorNode(PredicateOperatorNode node, Void unused) {
            List<PredicateNode> list = node.getPredicateArguments().stream()
                    .map(predNode -> (PredicateNode) modifyNode(predNode)).collect(Collectors.toList());
            node.setPredicateList(list);
            return node;
        }

        @Override
        public Node visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void unused) {
            final List<ExprNode> argumentList = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) modifyNode(exprNode)).collect(Collectors.toList());
            node.setArgumentsList(argumentList);
            return node;
        }

        @Override
        public Node visitExprOperatorNode(ExpressionOperatorNode node, Void unused) {
            final List<ExprNode> arguments = node.getExpressionNodes().stream()
                    .map(exprNode -> (ExprNode) modifyNode(exprNode)).collect(Collectors.toList());
            node.setExpressionList(arguments);
            return node;

        }

        @Override
        public Node visitIdentifierExprNode(IdentifierExprNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void unused) {
            Node arg = modifyNode(node.getPredicate());
            node.setArg((PredicateNode) arg);
            return node;
        }

        @Override
        public Node visitNumberNode(NumberNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitSelectSubstitutionNode(SelectSubstitutionNode node, Void unused) {
            return visitIfOrSelectNode(node);
        }

        @Override
        public Node visitIfSubstitutionNode(IfSubstitutionNode node, Void unused) {
            return visitIfOrSelectNode(node);
        }

        private Node visitIfOrSelectNode(AbstractIfAndSelectSubstitutionsNode node) {
            node.setConditions(
                    node.getConditions().stream().map(t -> (PredicateNode) modifyNode(t)).collect(Collectors.toList()));
            node.setSubstitutions(node.getSubstitutions().stream().map(t -> (SubstitutionNode) modifyNode(t))
                    .collect(Collectors.toList()));
            if (null != node.getElseSubstitution()) {
                SubstitutionNode elseSub = (SubstitutionNode) modifyNode(node.getElseSubstitution());
                node.setElseSubstitution(elseSub);
            }
            return node;
        }

        @Override
        public Node visitConditionSubstitutionNode(ConditionSubstitutionNode node, Void unused) {
            node.setCondition((PredicateNode) modifyNode(node.getCondition()));
            node.setSubstitution((SubstitutionNode) modifyNode(node.getSubstitution()));
            return node;
        }

        @Override
        public Node visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void unused) {
            node.setValue((ExprNode) modifyNode(node.getValue()));
            return node;
        }

        @Override
        public Node visitAnySubstitution(AnySubstitutionNode node, Void unused) {
            node.setPredicate((PredicateNode) modifyNode(node.getWherePredicate()));
            node.setSubstitution((SubstitutionNode) modifyNode(node.getThenSubstitution()));
            return node;
        }

        @Override
        public Node visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void unused) {
            List<SubstitutionNode> substitutions = node.getSubstitutions().stream()
                    .map(sub -> (SubstitutionNode) modifyNode(sub)).collect(Collectors.toList());
            node.setSubstitutions(substitutions);
            return node;
        }

        @Override
        public Node visitIdentifierPredicateNode(IdentifierPredicateNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void unused) {
            PredicateNode visitPredicateNode = (PredicateNode) modifyNode(node.getPredicateNode());
            node.setPredicate(visitPredicateNode);
            ExprNode expr = (ExprNode) modifyNode(node.getExpressionNode());
            node.setExpr(expr);
            return node;
        }

        @Override
        public Node visitSetComprehensionNode(SetComprehensionNode node, Void unused) {
            PredicateNode visitPredicateNode = (PredicateNode) modifyNode(node.getPredicateNode());
            node.setPredicate(visitPredicateNode);
            return node;
        }

        @Override
        public Node visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void unused) {
            PredicateNode pred = (PredicateNode) modifyNode(node.getPredicateNode());
            node.setPredicate(pred);
            return node;
        }

        @Override
        public Node visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Void unused) {
            node.setPredicate((PredicateNode) modifyNode(node.getPredicate()));
            return node;
        }

        @Override
        public Node visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Void unused) {
            node.setExpression((ExprNode) modifyNode(node.getExpression()));
            return node;
        }

        @Override
        public Node visitSkipSubstitutionNode(SkipSubstitutionNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitEnumerationSetNode(EnumerationSetNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitDeferredSetNode(DeferredSetNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitEnumeratedSetElementNode(EnumeratedSetElementNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, Void unused) {
            node.setLTLNode((LTLNode) modifyNode(node.getArgument()));
            return node;
        }

        @Override
        public Node visitLTLKeywordNode(LTLKeywordNode node, Void unused) {
            return node;
        }

        @Override
        public Node visitLTLInfixOperatorNode(LTLInfixOperatorNode node, Void unused) {
            node.setLeft((LTLNode) modifyNode(node.getLeft()));
            node.setRight((LTLNode) modifyNode(node.getRight()));
            return node;
        }

        @Override
        public Node visitLTLBPredicateNode(LTLBPredicateNode node, Void unused) {
            node.setPredicateNode((PredicateNode) modifyNode((PredicateNode) node.getPredicate()));
            return node;
        }

    }

}
