package de.bmoth.parser.ast.nodes;

public class SingleAssignSubstitution extends SubstitutionNode {

	IdentifierExprNode identifier;
	ExprNode value;

	public SingleAssignSubstitution(IdentifierExprNode identifier, ExprNode expr) {
		this.identifier = identifier;
		this.value = expr;
	}

}
