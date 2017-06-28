package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;
import org.antlr.v4.runtime.tree.ParseTree;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ConvertPsiNotBformulaToPsiNegatedBformula extends AbstractASTTransformation{

	@Override
	public boolean canHandleNode(Node node) {
		return node instanceof LTLPrefixOperatorNode;
	}

	@Override
	public Node transformNode(Node node) {
		LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) node;
        if (notOperator.getKind() == LTLPrefixOperatorNode.Kind.NOT) {
            LTLNode argument = notOperator.getArgument();
            if (argument instanceof LTLBPredicateNode) {
                LTLBPredicateNode bPredicateNode = (LTLBPredicateNode) argument;
                PredicateNode bPredicate = bPredicateNode.getPredicate();
                ParseTree parseTree = bPredicate.getParseTree();
                PredicateNode notNode = new PredicateOperatorNode(parseTree, PredicateOperatorNode.PredicateOperator.NOT, Arrays.asList(bPredicate));
                return new LTLBPredicateNode(notNode);
            }
        }
        return node;
	}

}
