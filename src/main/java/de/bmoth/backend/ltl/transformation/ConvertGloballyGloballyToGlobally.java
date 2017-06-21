package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertGloballyGloballyToGlobally extends AbstractASTTransformation{

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode globallyOperator01 = (LTLPrefixOperatorNode) oldNode;
        if (globallyOperator01.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
            LTLNode argument = globallyOperator01.getArgument();
            if (argument instanceof LTLPrefixOperatorNode) {
                LTLPrefixOperatorNode Operator02 = (LTLPrefixOperatorNode) argument;
                // case GG->G
                if (Operator02.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
                    setChanged();
                    return Operator02;
                }
                // case GnG->nG
                if (Operator02.getKind() != LTLPrefixOperatorNode.Kind.NOT) {
                        LTLNode argument02 = Operator02.getArgument();
                        if (argument02 instanceof LTLPrefixOperatorNode) {
                            LTLPrefixOperatorNode Operator03 = (LTLPrefixOperatorNode) argument02;
                            if (Operator03.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
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
