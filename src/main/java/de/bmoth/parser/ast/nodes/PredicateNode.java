package de.bmoth.parser.ast.nodes;

import java.util.Arrays;

import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator;

public abstract class PredicateNode extends TypedNode {

    public PredicateNode(ParseTree context) {
        super(context);
    }

    public PredicateNode getNegatedPredicateNode() {
        return new PredicateOperatorNode(getParseTree(), PredicateOperator.NOT, Arrays.asList(this));
    }

}
