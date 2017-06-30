package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.contains;
import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.AND;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNotOrToAndNot extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && contains(node, OR);
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode outerNot = (LTLPrefixOperatorNode) node;
        LTLInfixOperatorNode innerOr = (LTLInfixOperatorNode) outerNot.getArgument();

        setChanged();

        return new LTLInfixOperatorNode(AND, new LTLPrefixOperatorNode(NOT, innerOr.getLeft()), new LTLPrefixOperatorNode(NOT, innerOr.getRight()));
    }
}
