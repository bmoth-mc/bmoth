package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser.CastPredicateExpressionContext;

public class CastPredicateExpressionNode extends ExprNode {
    private PredicateNode predicate;

    public CastPredicateExpressionNode(CastPredicateExpressionContext ctx, PredicateNode predicate) {
        super(ctx);
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

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        return this.predicate.equalAst(((CastPredicateExpressionNode) other).predicate);
    }
}
