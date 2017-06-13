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

public class IfSubstitutionNode extends AbstractIfAndSelectSubstitutionsNode {

    public IfSubstitutionNode(List<PredicateNode> conditions, List<SubstitutionNode> substitutions,
            SubstitutionNode elseSubstitution) {
        super(conditions, substitutions, elseSubstitution);
    }

    @Override
    public String toString() {
        return prepareToString("IF", "ELSIF");
    }
}
