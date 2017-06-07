package de.bmoth.parser.ast.nodes;

import java.util.List;

public class SelectSubstitutionNode extends AbstractConditionsAndSubstitutionsNode {

    public SelectSubstitutionNode(List<PredicateNode> conditions, List<SubstitutionNode> substitutions,
            SubstitutionNode elseSubstitution) {
        super(conditions, substitutions, elseSubstitution);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(conditions.get(0)).append(" THEN ").append(substitutions.get(0));
        for (int i = 1; i < conditions.size(); i++) {
            sb.append(" WHEN ").append(conditions.get(i)).append(" THEN ").append(substitutions.get(i));
        }
        if (null != elseSubstitution) {
            sb.append(" ELSE ").append(elseSubstitution);
        }
        sb.append(" END");
        return sb.toString();
    }

}
