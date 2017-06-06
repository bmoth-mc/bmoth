package de.bmoth.parser.ast.nodes;

import java.util.HashSet;

public class SkipSubstitutionNode extends SubstitutionNode {

    public SkipSubstitutionNode() {
        setAssignedVariables(new HashSet<>());
    }
}