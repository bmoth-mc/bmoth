package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertNotFinallyToGloballyNot extends AbstractASTTransformation{

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
                LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) argument;
                if (finallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
                    LTLPrefixOperatorNode newNot = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT,
                            finallyOperator.getArgument());
                    setChanged();
                    return new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.GLOBALLY, newNot);
                }
            }
        }
        return oldNode;
	}

}
