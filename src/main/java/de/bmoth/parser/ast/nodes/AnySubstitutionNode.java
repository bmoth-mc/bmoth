package de.bmoth.parser.ast.nodes;

import java.util.List;

public class AnySubstitutionNode extends SubstitutionNode {

    private final List<DeclarationNode> parameters;
    private final PredicateNode wherePredicate;
    private final SubstitutionNode thenSubstitution;

    public AnySubstitutionNode(List<DeclarationNode> parameters, PredicateNode wherePredicate,
            SubstitutionNode thenSubstitution) {
        this.parameters = parameters;
        this.wherePredicate = wherePredicate;
        this.thenSubstitution = thenSubstitution;
        super.setAssignedVariables(thenSubstitution.getAssignedVariables());
    }

    public List<DeclarationNode> getParameters() {
        return parameters;
    }

    public PredicateNode getWherePredicate() {
        return wherePredicate;
    }

    public SubstitutionNode getThenSubstitution() {
        return thenSubstitution;
    }

}
