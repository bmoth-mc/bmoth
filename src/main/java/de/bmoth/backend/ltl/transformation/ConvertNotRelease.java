package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.LTLASTTransformation;

public class ConvertNotRelease extends LTLASTTransformation{

	@Override
	public boolean canHandleNode(Node node) {
		return node instanceof LTLPrefixOperatorNode;
	}

	@Override
	public Node transformNode(Node oldNode) {
		LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) oldNode;
        if (notOperator.getKind() == LTLPrefixOperatorNode.Kind.NOT) {
            LTLNode argument = notOperator.getArgument();
            LTLInfixOperatorNode releaseOperator = (LTLInfixOperatorNode) argument;
            if(releaseOperator.getKind() == LTLInfixOperatorNode.Kind.RELEASE){
            	LTLNode left=releaseOperator.getLeft();
            	LTLNode right = releaseOperator.getRight();
            	LTLNode notLeft = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT,left);
            	LTLNode notRight = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT, right);
            	setChanged();
            	return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.UNTIL, notLeft, notRight);
            }
        }
		return oldNode;
	}

}
