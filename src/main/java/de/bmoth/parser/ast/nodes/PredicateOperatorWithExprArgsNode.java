package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.PredicateOperatorWithExprArgsContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

public class PredicateOperatorWithExprArgsNode extends PredicateNode implements OperatorNode<PredicateOperatorWithExprArgsNode.PredOperatorExprArgs> {

    public enum PredOperatorExprArgs {
        EQUAL, NOT_EQUAL, ELEMENT_OF, LESS_EQUAL, LESS, GREATER_EQUAL, GREATER//
        , NOT_BELONGING, INCLUSION, STRICT_INCLUSION, NON_INCLUSION, STRICT_NON_INCLUSION//
    }

    private static final Map<Integer, PredOperatorExprArgs> map = new HashMap<>();

    static {
        map.put(BMoThParser.EQUAL, PredOperatorExprArgs.EQUAL);
        map.put(BMoThParser.NOT_EQUAL, PredOperatorExprArgs.NOT_EQUAL);
        map.put(BMoThParser.ELEMENT_OF, PredOperatorExprArgs.ELEMENT_OF);
        map.put(BMoThParser.COLON, PredOperatorExprArgs.ELEMENT_OF);
        map.put(BMoThParser.LESS_EQUAL, PredOperatorExprArgs.LESS_EQUAL);
        map.put(BMoThParser.LESS, PredOperatorExprArgs.LESS);
        map.put(BMoThParser.GREATER_EQUAL, PredOperatorExprArgs.GREATER_EQUAL);
        map.put(BMoThParser.GREATER, PredOperatorExprArgs.GREATER);
        map.put(BMoThParser.NOT_BELONGING, PredOperatorExprArgs.NOT_BELONGING);
        map.put(BMoThParser.INCLUSION, PredOperatorExprArgs.INCLUSION);
        map.put(BMoThParser.STRICT_INCLUSION, PredOperatorExprArgs.STRICT_INCLUSION);
        map.put(BMoThParser.NON_INCLUSION, PredOperatorExprArgs.NON_INCLUSION);
        map.put(BMoThParser.STRICT_NON_INCLUSION, PredOperatorExprArgs.STRICT_NON_INCLUSION);
    }

    private List<ExprNode> expressionNodes;
    private String operatorString;
    private PredOperatorExprArgs operator;

    public PredicateOperatorWithExprArgsNode(PredicateOperatorWithExprArgsContext ctx, List<ExprNode> expressionNodes) {
        super(ctx);
        this.expressionNodes = expressionNodes;
        this.operatorString = ctx.operator.getText();
        this.operator = loopUpOperator(ctx.operator.getType());

    }

    public PredicateOperatorWithExprArgsNode(ParseTree ctx, PredOperatorExprArgs operator,
            List<ExprNode> expressionNodes) {
        super(ctx);
        this.expressionNodes = expressionNodes;
        this.operator = operator;
    }

    private PredOperatorExprArgs loopUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented: " + operatorString);
    }

    @Override
    public PredOperatorExprArgs getOperator() {
        return operator;
    }

    @Override
    public void setOperator(PredOperatorExprArgs operator) {
        this.operator = operator;
    }

    public List<ExprNode> getExpressionNodes() {
        return expressionNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.operator.name()).append("(");
        Iterator<ExprNode> iter = expressionNodes.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().toString());
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public void setArgumentsList(List<ExprNode> argumentList) {
        this.expressionNodes = argumentList;
    }

    @Override
    public boolean equalAst(Node other) {
        return sameClass(other)
            && new ListAstEquals<ExprNode>().equalAst(expressionNodes, ((PredicateOperatorWithExprArgsNode) other).expressionNodes);
    }
}
