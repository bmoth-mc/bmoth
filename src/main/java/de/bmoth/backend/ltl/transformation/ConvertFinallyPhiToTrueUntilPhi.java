package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertFinallyPhiToTrueUntilPhi extends AbstractASTTransformation{

	@Override
	public boolean canHandleNode(Node node) {
		return node instanceof LTLPrefixOperatorNode;
	}

	@Override
	public Node transformNode(Node oldNode) {
		LTLPrefixOperatorNode finallyOperator = (LTLPrefixOperatorNode) oldNode;
        if (finallyOperator.getKind() == LTLPrefixOperatorNode.Kind.FINALLY) {
            LTLNode argument = finallyOperator.getArgument();
            LTLKeywordNode trueNode = new LTLKeywordNode(LTLKeywordNode.Kind.TRUE);
            return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.UNTIL, trueNode, argument);
        }
		return oldNode;
	}

}
