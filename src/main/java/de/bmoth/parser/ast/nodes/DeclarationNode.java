package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.Token;

public class DeclarationNode extends TypedNode {

	private String name;

	public DeclarationNode(Token token, String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
