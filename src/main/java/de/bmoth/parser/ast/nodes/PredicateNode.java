package de.bmoth.parser.ast.nodes;

import java.util.Arrays;
import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;

public abstract class PredicateNode extends TypedNode {

    public PredicateNode(ParseTree context) {
        super(context);
    }

    public PredicateNode getNegatedPredicateNode() {
        if (getClass().equals(PredicateOperatorNode.class)
            && ((PredicateOperatorNode) this).getOperator() == PredicateOperator.NOT) {
            return ((PredicateOperatorNode) this).getPredicateArguments().get(0);
        }
        return new PredicateOperatorNode(getParseTree(), PredicateOperator.NOT, Arrays.asList(this));
    }
}
