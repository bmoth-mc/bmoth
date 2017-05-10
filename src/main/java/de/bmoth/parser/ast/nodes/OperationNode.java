package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public class OperationNode {

	private final String name;
	private final SubstitutionNode substitution;

	public OperationNode(ParseTree parseTree, String name, SubstitutionNode substitution) {
		this.name = name;
		this.substitution = substitution;
	}

	public String getName() {
		return name;
	}

	public SubstitutionNode getSubstitution() {
		return substitution;
	}

    @Override
    public String toString() {
        if (substitution instanceof SingleAssignSubstitutionNode){
            return name + " = BEGIN " + substitution + " END";
        } else {
            return name + " = " + substitution;
        }
    }
}
