package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertGloballyPhiToPhiWeakUntilFalse extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) oldNode;
        if (globallyOperator.getKind() == LTLPrefixOperatorNode.Kind.GLOBALLY) {
            LTLNode argument = globallyOperator.getArgument();
            return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.WEAK_UNTIL, argument, new LTLKeywordNode(LTLKeywordNode.Kind.FALSE));
        }
        return oldNode;
    }

}
