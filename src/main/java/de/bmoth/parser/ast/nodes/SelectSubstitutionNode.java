package de.bmoth.parser.ast.nodes;

public class SelectSubstitutionNode extends SubstitutionNode {

	private final SubstitutionNode substitution;
	private final PredicateNode condition;

	public SelectSubstitutionNode(PredicateNode condition, SubstitutionNode substitution) {
		this.condition = condition;
		this.substitution = substitution;
	}

	public SubstitutionNode getSubstitution() {
		return substitution;
	}

	public PredicateNode getCondition() {
		return condition;
	}

}
