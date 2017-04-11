package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.Token;

public class IdentifierExprNode extends ExprNode{

	final Token token;
	final String name;
	final DeclarationNode declarationNode;
	
	public IdentifierExprNode(Token token, DeclarationNode declarationNode) {
		this.token = token;
		this.name = token.getText();
		this.declarationNode = declarationNode;
	}

}
