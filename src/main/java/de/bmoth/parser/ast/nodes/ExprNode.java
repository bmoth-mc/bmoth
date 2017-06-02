package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public abstract class ExprNode extends TypedNode implements Node {

    public ExprNode(ParseTree parseTree) {
        super(parseTree);
    }

}
