package de.bmoth.parser.ast.nodes;

import java.util.HashSet;

public class SkipSubstitutionNode extends SubstitutionNode {

    public SkipSubstitutionNode() {
        setAssignedVariables(new HashSet<>());
    }

    @Override
    public boolean equalAst(Node other) {
        return NodeUtil.isSameClass(this, other);
    }

    public String toString() {
        return "skip";
    }
}
