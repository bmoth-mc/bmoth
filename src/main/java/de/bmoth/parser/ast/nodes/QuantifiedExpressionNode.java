package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionContext;
import de.bmoth.antlr.BMoThParser.SetComprehensionExpressionContext;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantifiedExpressionNode extends ExprNode {

    public static enum QuatifiedExpressionOperator {
        SET_COMPREHENSION, QUANTIFIED_UNION, QUANTIFIED_INTER
    }

    private static final Map<Integer, QuatifiedExpressionOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.QUANTIFIED_UNION, QuatifiedExpressionOperator.QUANTIFIED_UNION);
        map.put(BMoThParser.QUANTIFIED_INTER, QuatifiedExpressionOperator.QUANTIFIED_INTER);

    }

    private final List<DeclarationNode> declarationList;
    private final PredicateNode predicateNode;
    private final ExprNode expressionNode;
    private QuatifiedExpressionOperator operator;

    public QuantifiedExpressionNode(ExpressionContext ctx, List<DeclarationNode> declarationList,
                                    PredicateNode predNode, ExprNode expressionNode, Token operator2) {
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = loopUpOperator(operator2.getType());
        this.expressionNode = expressionNode;
    }

    private QuatifiedExpressionOperator loopUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented");
    }

    public QuantifiedExpressionNode(SetComprehensionExpressionContext ctx, List<DeclarationNode> declarationList,
                                    PredicateNode predNode, ExprNode expressionNode, QuatifiedExpressionOperator setComprehension) {
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

    public QuatifiedExpressionOperator getOperator() {
        return operator;
    }

    public ExprNode getExpressionNode() {
        return expressionNode;
    }

}
