package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

import static de.bmoth.backend.ltl.LTLTransformationUtil.isOperator;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NOT;

public class ConvertNotFormulaToNegatedBFormula implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return isOperator(node, NOT) && ((LTLPrefixOperatorNode) node).getArgument() instanceof LTLBPredicateNode;
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) node;
        LTLNode argument = notOperator.getArgument();
        LTLBPredicateNode bPredicateNode = (LTLBPredicateNode) argument;
        PredicateNode negatedPredicate = bPredicateNode.getPredicate().getNegatedPredicateNode();
        return new LTLBPredicateNode(negatedPredicate);
    }
}
