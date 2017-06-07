package de.bmoth.parser.ast.nodes;

import java.util.List;

/**
 * IF conditional substitution:
 * 
 * <pre>
 *  IF P1 THEN S1 
 *  ELSIF P2 THEN S2 
 *  ... 
 *  ELSE Sn 
 *  END
 * </pre>
 * 
 * All ELSIF-THEN pairs and the ELSE part are optional.
 * 
 */

public class IfSubstitutionNode extends AbstractConditionsAndSubstitutionsNode {

    public IfSubstitutionNode(List<PredicateNode> conditions, List<SubstitutionNode> substitutions,
            SubstitutionNode elseSubstitution) {
        super(conditions, substitutions, elseSubstitution);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IF ").append(conditions.get(0)).append(" THEN ").append(substitutions.get(0));
        for (int i = 1; i < conditions.size(); i++) {
            sb.append(" ELSIF ").append(conditions.get(i)).append(" THEN ").append(substitutions.get(i));
        }
        if (null != elseSubstitution) {
            sb.append(" ELSE ").append(elseSubstitution);
        }
        sb.append(" END");
        return sb.toString();
    }
}
