package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantifiedExpressionNode extends SetComprehensionNode {

    public enum QuantifiedExpressionOperator {
        QUANTIFIED_UNION, QUANTIFIED_INTER
    }

    private static final Map<Integer, QuantifiedExpressionOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.QUANTIFIED_UNION, QuantifiedExpressionOperator.QUANTIFIED_UNION);
        map.put(BMoThParser.QUANTIFIED_INTER, QuantifiedExpressionOperator.QUANTIFIED_INTER);

    }

    private QuantifiedExpressionOperator operator;
    private ExprNode expressionNode;

    public ExprNode getExpressionNode() {
        return expressionNode;
    }


    public void setExpr(ExprNode expr) {
        this.expressionNode = expr;
    }


    public QuantifiedExpressionNode(ExpressionContext ctx, List<DeclarationNode> declarationList,
                                    PredicateNode predNode, ExprNode expressionNode, Token operator2) {
        super(ctx, declarationList, predNode);
        this.expressionNode = expressionNode;
        this.operator = loopUpOperator(operator2.getType());
    }

    private QuantifiedExpressionOperator loopUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented");
    }

    public QuantifiedExpressionOperator getOperator() {
        return operator;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        QuantifiedExpressionNode that = (QuantifiedExpressionNode) other;
        return this.operator.equals(that.operator)
            && getExpressionNode().equalAst(that.getExpressionNode())
            && this.expressionNode.equalAst(that.expressionNode)
            && NodeUtil.equalAst(getDeclarationList(), that.getDeclarationList());

    }
}
