package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.RELEASE;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.UNTIL;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNotRelease implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, RELEASE);
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode not = (LTLPrefixOperatorNode) node;
        LTLInfixOperatorNode release = (LTLInfixOperatorNode) not.getArgument();
        LTLNode innerLeft = release.getLeft();
        LTLNode innerRight = release.getRight();

        return new LTLInfixOperatorNode(UNTIL, new LTLPrefixOperatorNode(NOT, innerLeft), new LTLPrefixOperatorNode(NOT, innerRight));
    }

}
