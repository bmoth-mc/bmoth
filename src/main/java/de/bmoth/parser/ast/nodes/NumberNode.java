package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.parser.ast.types.IntegerType;

public class NumberNode extends ExprNode {

	final int value;

	public NumberNode(ParseTree ctx, int value) {
		this.value = value;
	}

}
