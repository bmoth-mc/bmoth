package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.GLOBALLY;

public class ConvertGloballyPhiToFalseReleasePhi implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, GLOBALLY);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLPrefixOperatorNode globallyOperator = (LTLPrefixOperatorNode) oldNode;
        LTLNode argument = globallyOperator.getArgument();
        return new LTLInfixOperatorNode(LTLInfixOperatorNode.Kind.RELEASE, new LTLKeywordNode(LTLKeywordNode.Kind.FALSE), argument);
    }
}
