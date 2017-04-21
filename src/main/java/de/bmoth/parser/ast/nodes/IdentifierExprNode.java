package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.Token;

public class IdentifierExprNode extends ExprNode {

	final Token token;
	final String name;
	private final DeclarationNode declarationNode;

	public IdentifierExprNode(Token token, DeclarationNode declarationNode) {
		this.token = token;
		this.name = token.getText();
		this.declarationNode = declarationNode;
	}

	public DeclarationNode getDeclarationNode() {
		return declarationNode;
	}

	@Override
	public String toString() {
		return name;
	}

}
