package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertFinallyFinallyToFinally extends AbstractASTTransformation{

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode finallyOperator01 = (LTLPrefixOperatorNode) oldNode;
        if (finallyOperator01.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
            LTLNode argument = finallyOperator01.getArgument();
            if (argument instanceof LTLPrefixOperatorNode) {
                LTLPrefixOperatorNode Operator02 = (LTLPrefixOperatorNode) argument;
                // case FF->F
                if (Operator02.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
                    setChanged();
                    return Operator02;
                }
                // case FnF->nF
                if (Operator02.getKind() != LTLPrefixOperatorNode.Kind.NOT) {
                        LTLNode argument02 = Operator02.getArgument();
                        if (argument02 instanceof LTLPrefixOperatorNode) {
                            LTLPrefixOperatorNode Operator03 = (LTLPrefixOperatorNode) argument02;
                            if (Operator03.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
                                setChanged();
                                return Operator02;
                            }
                        }
                }
            }
        }
        return oldNode;
    }
}
