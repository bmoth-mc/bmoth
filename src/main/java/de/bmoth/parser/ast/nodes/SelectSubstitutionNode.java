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

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        SelectSubstitutionNode that = (SelectSubstitutionNode) other;
        return new ListAstEquals<PredicateNode>().equalAst(this.getConditions(), that.getConditions())
            && new ListAstEquals<SubstitutionNode>().equalAst(this.getSubstitutions(), that.getSubstitutions())
            && this.getElseSubstitution().equalAst(that.getElseSubstitution());
    }
}
