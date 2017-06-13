package de.bmoth.parser.ast.nodes;

import java.util.List;

public class SelectSubstitutionNode extends AbstractIfAndSelectSubstitutionsNode {

    public SelectSubstitutionNode(List<PredicateNode> conditions, List<SubstitutionNode> substitutions,
            SubstitutionNode elseSubstitution) {
        super(conditions, substitutions, elseSubstitution);
    }

    @Override
    public String toString() {
        return prepareToString("SELECT", "WHEN");
    }

}
