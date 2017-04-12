package de.bmoth.parser.ast.nodes;

public class SingleAssignSubstitution extends SubstitutionNode {

	private final IdentifierExprNode identifier;
	private final ExprNode value;

	public SingleAssignSubstitution(IdentifierExprNode identifier, ExprNode expr) {
		this.identifier = identifier;
		this.value = expr;
	}

	public IdentifierExprNode getIdentifier() {
		return identifier;
	}

	public ExprNode getValue() {
		return value;
	}

}
