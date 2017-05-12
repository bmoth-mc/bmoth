package de.bmoth.parser.ast.nodes;

import java.util.HashSet;
import java.util.Set;

public class SingleAssignSubstitutionNode extends SubstitutionNode {

    private final IdentifierExprNode identifier;
    private final ExprNode value;

    public SingleAssignSubstitutionNode(IdentifierExprNode identifier, ExprNode expr) {
        this.identifier = identifier;
        this.value = expr;
        Set<DeclarationNode> set = new HashSet<>();
        set.add(identifier.getDeclarationNode());
        super.setAssignedVariables(set);
    }

    public IdentifierExprNode getIdentifier() {
        return identifier;
    }

    public ExprNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return identifier + " := " + value;
    }
}
