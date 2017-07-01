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
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        SelectSubstitutionNode that = (SelectSubstitutionNode) other;
        return NodeUtil.equalAst(this.getConditions(), that.getConditions())
            && NodeUtil.equalAst(this.getSubstitutions(), that.getSubstitutions())
            && this.getElseSubstitution().equalAst(that.getElseSubstitution());
    }
}
