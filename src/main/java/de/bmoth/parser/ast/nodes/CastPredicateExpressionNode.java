package de.bmoth.parser.ast.nodes;

public class CastPredicateExpressionNode extends ExprNode {
    private PredicateNode predicate;

    public CastPredicateExpressionNode(PredicateNode predicate) {
        this.predicate = predicate;
    }

    public PredicateNode getPredicate() {
        return predicate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("bool(");
        sb.append(predicate.toString());
        sb.append(")");
        return sb.toString();
    }

    public void setArg(PredicateNode arg) {
        this.predicate = arg;
    }
}