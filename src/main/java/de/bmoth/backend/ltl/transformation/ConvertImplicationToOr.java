package de.bmoth.backend.ltl.transformation;


import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.IMPLICATION;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertImplicationToOr implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, IMPLICATION);
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLInfixOperatorNode implicationOperator = (LTLInfixOperatorNode) oldNode;
        LTLNode innerLeft = implicationOperator.getLeft();
        LTLNode innerRight = implicationOperator.getRight();
        return new LTLInfixOperatorNode(OR, new LTLPrefixOperatorNode(NOT, innerLeft), innerRight);
    }
}
