package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public abstract class PredicateNode extends TypedNode {

    public PredicateNode(ParseTree context) {
        super(context);
    }

}
