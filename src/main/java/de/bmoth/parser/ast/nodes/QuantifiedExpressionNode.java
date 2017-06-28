package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import de.bmoth.antlr.BMoThParser.SetComprehensionExpressionContext;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantifiedExpressionNode extends ExprNode {

    public enum QuantifiedExpressionOperator {
        SET_COMPREHENSION, QUANTIFIED_UNION, QUANTIFIED_INTER
    }

    private static final Map<Integer, QuantifiedExpressionOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.QUANTIFIED_UNION, QuantifiedExpressionOperator.QUANTIFIED_UNION);
        map.put(BMoThParser.QUANTIFIED_INTER, QuantifiedExpressionOperator.QUANTIFIED_INTER);

    }

    private List<DeclarationNode> declarationList;
    private PredicateNode predicateNode;
    private ExprNode expressionNode;
    private QuantifiedExpressionOperator operator;

    public QuantifiedExpressionNode(ExpressionContext ctx, List<DeclarationNode> declarationList,
                                    PredicateNode predNode, ExprNode expressionNode, Token operator2) {
        super(ctx);
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = loopUpOperator(operator2.getType());
        this.expressionNode = expressionNode;
    }

    private QuantifiedExpressionOperator loopUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented");
    }

    public QuantifiedExpressionNode(SetComprehensionExpressionContext ctx, List<DeclarationNode> declarationList,
                                    PredicateNode predNode, ExprNode expressionNode, QuantifiedExpressionOperator setComprehension) {
        super(ctx);
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = setComprehension;
        this.expressionNode = expressionNode;
    }

    public List<DeclarationNode> getDeclarationList() {
        return declarationList;
    }

    public PredicateNode getPredicateNode() {
        return predicateNode;
    }

    public QuantifiedExpressionOperator getOperator() {
        return operator;
    }

    public ExprNode getExpressionNode() {
        return expressionNode;
    }

    public void setExpr(ExprNode expr) {
        this.expressionNode = expr;
    }

    public void setPredicate(PredicateNode node) {
        this.predicateNode = node;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        QuantifiedExpressionNode that = (QuantifiedExpressionNode) other;
        return this.operator.equals(that.operator)
            && this.expressionNode.equalAst(that.expressionNode)
            && this.predicateNode.equalAst(that.predicateNode)
            && new ListAstEquals<DeclarationNode>().equalAst(this.declarationList, that.declarationList);

    }
}
