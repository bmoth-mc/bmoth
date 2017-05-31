package de.bmoth.parser.ast.nodes;

import java.util.HashSet;
import java.util.Set;

public class OperationNode {

    private final String name;
    private final SubstitutionNode substitution;

    public OperationNode(String name, SubstitutionNode substitution) {
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
        if (substitution instanceof SingleAssignSubstitutionNode) {
            return name + " = BEGIN " + substitution + " END";
        } else {
            return name + " = " + substitution;
        }
    }

    public Set<DeclarationNode> getAssignedDeclarationNodes() {
        return new HashSet<>(this.substitution.getAssignedVariables());
    }
}
