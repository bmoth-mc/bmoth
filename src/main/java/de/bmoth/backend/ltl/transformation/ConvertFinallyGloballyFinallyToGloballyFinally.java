package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertFinallyGloballyFinallyToGloballyFinally implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldnode) {
        LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) oldnode;
        if (finallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
            LTLNode argument = finallyOperator.getArgument();
            if (argument instanceof LTLPrefixOperatorNode) {
                LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) argument;
                if (globallyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
                    LTLNode argument2 = globallyOperator.getArgument();
                    if (argument2 instanceof LTLPrefixOperatorNode) {
                        LTLPrefixOperatorNode innerFinallyOperator = (LTLPrefixOperatorNode) argument2;
                        if (innerFinallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
                            LTLPrefixOperatorNode newFinally = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.FINALLY,
                                innerFinallyOperator.getArgument());
                            return new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.GLOBALLY, newFinally);
                        }
                    }
                }
            }
        }
        return oldnode;
    }

}
