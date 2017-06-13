package de.bmoth.parser.ast.nodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Super class of {@link IfSubstitutionNode} and {@link SelectSubstitutionNode}.
 * Both lists {@code conditions} and {@code substitutions} have the same size
 * and at least one element. {@code elseSubstitution} can be {@code null}.
 * 
 **/
public abstract class AbstractIfAndSelectSubstitutionsNode extends SubstitutionNode {
    protected List<PredicateNode> conditions;
    protected List<SubstitutionNode> substitutions;
    protected SubstitutionNode elseSubstitution;

    /**
     * Constructor called by the subclasses.
     * 
     * @param conditions
     *            the list of conditions
     * @param substitutions
     *            the list of substitution
     * @param elseSubstitution
     *            the else substitution; maybe {@code null}
     */
    public AbstractIfAndSelectSubstitutionsNode(List<PredicateNode> conditions, List<SubstitutionNode> substitutions,
            SubstitutionNode elseSubstitution) {
        this.conditions = conditions;
        this.substitutions = substitutions;
        this.elseSubstitution = elseSubstitution;
        Set<DeclarationNode> assignedVariables = new HashSet<>();
        substitutions.forEach(t -> assignedVariables.addAll(t.getAssignedVariables()));
        setAssignedVariables(assignedVariables);
    }

    public List<SubstitutionNode> getSubstitutions() {
        return this.substitutions;
    }

    public SubstitutionNode getElseSubstitution() {
        return this.elseSubstitution;
    }

    public List<PredicateNode> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<PredicateNode> conditions) {
        this.conditions = conditions;
    }

    public void setSubstitutions(List<SubstitutionNode> substitutions) {
        this.substitutions = substitutions;
    }

    public void setElseSubstitution(SubstitutionNode elseSub) {
        this.elseSubstitution = elseSub;
    }

    String prepareToString(String selectIf, String whenElsif) {
        StringBuilder sb = new StringBuilder();
        sb.append(selectIf).append(" ").append(conditions.get(0)).append(" THEN ").append(substitutions.get(0));
        for (int i = 1; i < conditions.size(); i++) {
            sb.append(" ").append(whenElsif).append(" ").append(conditions.get(i)).append(" THEN ").append(substitutions.get(i));
        }
        if (null != elseSubstitution) {
            sb.append(" ELSE ").append(elseSubstitution);
        }
        sb.append(" END");
        return sb.toString();
    }

}
