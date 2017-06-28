package de.bmoth.parser.ast.nodes;

public class ConditionSubstitutionNode extends SubstitutionNode {
    private Kind kind;
    private PredicateNode condition;
    private SubstitutionNode substitution;

    public enum Kind {
        PRECONDITION, ASSERT
    }

    public ConditionSubstitutionNode(Kind kind, PredicateNode condition, SubstitutionNode substitution) {
        this.condition = condition;
        this.substitution = substitution;
        this.kind = kind;
        super.setAssignedVariables(substitution.getAssignedVariables());
    }

    public SubstitutionNode getSubstitution() {
        return substitution;
    }

    public PredicateNode getCondition() {
        return condition;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {

        return "SELECT " + condition + " THEN " + substitution + " END";
    }

    public void setSubstitution(SubstitutionNode substitution) {
        this.substitution = substitution;
    }

    public void setCondition(PredicateNode predicate) {
        this.condition = predicate;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        ConditionSubstitutionNode that = (ConditionSubstitutionNode) other;
        return this.kind.equals(that.kind)
            && this.condition.equalAst(that.condition)
            && this.substitution.equalAst(that.substitution);
    }
}
