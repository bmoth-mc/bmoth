package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public class NumberNode extends ExprNode {

	final int value;

	public NumberNode(ParseTree ctx, int value) {
		this.value = value;
	}

}
