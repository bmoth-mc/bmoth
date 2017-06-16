package de.bmoth.backend.ltl;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTLTransformations {
    private static LTLTransformations instance;

    private final List<AbstractASTTransformation> transformationList;

    private LTLTransformations() {
        this.transformationList = new ArrayList<>();
        transformationList.add(new ConvertNotGloballyToFinallyNot());
    }

    public static LTLTransformations getInstance() {
        if (null == instance) {
            instance = new LTLTransformations();
        }
        return instance;
    }

    public static LTLNode transformLTLNode(LTLFormula ltlFormula) {
        LTLTransformations astTransformationForZ3 = LTLTransformations.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformLTLNode(ltlFormula.getFormula());
    }

    private class ConvertNotGloballyToFinallyNot extends AbstractASTTransformation {

        @Override
        public boolean canHandleNode(Node node) {
            return node instanceof LTLPrefixOperatorNode;
        }

        @Override
        public Node transformNode(Node oldNode) {
            LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) oldNode;
            if (notOperator.getKind() == LTLPrefixOperatorNode.Kind.NOT) {
                LTLNode argument = notOperator.getArgument();
                if (argument instanceof LTLPrefixOperatorNode) {
                    LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) argument;
                    if (globallyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
                        LTLPrefixOperatorNode newNot = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT,
                                globallyOperator.getArgument());
                        setChanged();
                        return new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.FINALLY, newNot);
                    }
                }
            }
            return oldNode;
        }

    }
}
