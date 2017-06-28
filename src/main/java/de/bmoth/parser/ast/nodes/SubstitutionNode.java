package de.bmoth.parser.ast.nodes;

import java.util.ArrayList;
import java.util.Set;

public abstract class SubstitutionNode implements Node {
    private Set<DeclarationNode> assignedVariables;

    public Set<DeclarationNode> getAssignedVariables() {
        return assignedVariables;
    }

    public void setAssignedVariables(Set<DeclarationNode> assignedVariables) {
        this.assignedVariables = assignedVariables;
    }
}
