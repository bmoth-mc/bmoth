package de.bmoth.parser.ast.nodes;

import java.util.List;

public class ParallelSubstitutionNode extends SubstitutionNode {

	private final List<SubstitutionNode> substitutions;

	public ParallelSubstitutionNode(List<SubstitutionNode> substitutions) {
		this.substitutions = substitutions;
	}

	public List<SubstitutionNode> getSubstitutions() {
		return substitutions;
	}
}
