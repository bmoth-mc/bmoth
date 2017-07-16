package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertGloballyFinallyGloballyToFinallyGlobally implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldnode) {
        LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) oldnode;
        if (globallyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
            LTLNode argument = globallyOperator.getArgument();
            if (argument instanceof LTLPrefixOperatorNode) {
                LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) argument;
                if (finallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
                    LTLNode argument2 = finallyOperator.getArgument();
                    if (argument2 instanceof LTLPrefixOperatorNode) {
                        LTLPrefixOperatorNode innerGloballyOperator = (LTLPrefixOperatorNode) argument2;
                        if (innerGloballyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
                            return finallyOperator;
                        }
                    }
                }
            }
        }
        return oldnode;
    }

}
