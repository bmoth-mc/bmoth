package de.bmoth.parser.ast.nodes;

import java.util.List;

public class AnySubstitutionNode extends SubstitutionNode {

    private List<DeclarationNode> parameters;
    private PredicateNode wherePredicate;
    private SubstitutionNode thenSubstitution;

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

    public void setPredicate(PredicateNode predNode) {
        this.wherePredicate = predNode;
    }

    public void setSubstitution(SubstitutionNode substitutionNode) {
        this.thenSubstitution = substitutionNode;
    }

    @Override
    public String toString() {
        return "ANY " + parameters + " WHERE " + wherePredicate + " THEN " + thenSubstitution + " END";
    }

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        AnySubstitutionNode that = (AnySubstitutionNode) other;
        return new ListAstEquals<DeclarationNode>().equalAst(this.parameters, that.parameters)
            && this.wherePredicate.equalAst(that.wherePredicate)
            && this.thenSubstitution.equalAst(that.thenSubstitution);
    }
}
