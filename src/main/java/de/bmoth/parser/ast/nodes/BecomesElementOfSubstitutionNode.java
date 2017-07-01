package de.bmoth.parser.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

public class BecomesElementOfSubstitutionNode extends SubstitutionNode {
    private List<IdentifierExprNode> identifiers;
    private ExprNode expression;

    public BecomesElementOfSubstitutionNode(List<IdentifierExprNode> identifiers, ExprNode expression) {
        this.identifiers = identifiers;
        this.expression = expression;
        super.setAssignedVariables(
                identifiers.stream().map(IdentifierExprNode::getDeclarationNode).collect(Collectors.toSet()));
    }

    public List<IdentifierExprNode> getIdentifiers() {
        return identifiers;
    }

    public ExprNode getExpression() {
        return expression;
    }

    public void setExpression(ExprNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return identifiers.stream().map(Object::toString).collect(Collectors.joining(",")) + " :( " + expression + ")";
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        BecomesElementOfSubstitutionNode that = (BecomesElementOfSubstitutionNode) other;
        return this.expression.equalAst(that.expression)
            && NodeUtil.equalAst(this.identifiers, that.identifiers);
    }
}
