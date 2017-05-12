package de.bmoth.parser.ast.nodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParallelSubstitutionNode extends SubstitutionNode {

    private final List<SubstitutionNode> substitutions;

    public ParallelSubstitutionNode(List<SubstitutionNode> substitutions) {
        Set<DeclarationNode> set = new HashSet<>();
        for (SubstitutionNode sub : substitutions) {
            set.addAll(sub.getAssignedVariables());
        }
        super.setAssignedVariables(set);
        this.substitutions = substitutions;
    }

    public List<SubstitutionNode> getSubstitutions() {
        return substitutions;
    }

}
