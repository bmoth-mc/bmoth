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

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        SubstitutionNode that = (SubstitutionNode) other;
        return new ListAstEquals<DeclarationNode>().equalAst(new ArrayList<>(this.assignedVariables), new ArrayList<>(that.assignedVariables));
    }
}
