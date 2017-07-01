package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.QuantifiedPredicateContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuantifiedPredicateNode extends PredicateNode {

    public enum QuantifiedPredicateOperator {
        UNIVERSAL_QUANTIFICATION, EXISTENTIAL_QUANTIFICATION
    }

    private static final Map<Integer, QuantifiedPredicateOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.FOR_ANY, QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION);
        map.put(BMoThParser.EXITS, QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION);
    }

    private List<DeclarationNode> declarationList;
    private PredicateNode predicateNode;
    private QuantifiedPredicateOperator operator;

    public QuantifiedPredicateNode(QuantifiedPredicateContext ctx, List<DeclarationNode> declarationList,
            PredicateNode predNode) {
        super(ctx);
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = lookUpOperator(ctx.operator.getType());
    }

    private QuantifiedPredicateOperator lookUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented");
    }

    public List<DeclarationNode> getDeclarationList() {
        return declarationList;
    }

    public PredicateNode getPredicateNode() {
        return predicateNode;
    }

    public QuantifiedPredicateOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (operator == QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION) {
            sb.append("EXISTS(");
        } else if (operator == QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION) {
            sb.append("FORALL(");
        }
        sb.append(declarationList.stream().map(Object::toString).collect(Collectors.joining(",")));
        sb.append(",");
        sb.append(predicateNode);
        sb.append(")");
        return sb.toString();
    }

    public void setPredicate(PredicateNode pred) {
        this.predicateNode = pred;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        QuantifiedPredicateNode that = (QuantifiedPredicateNode) other;
        return this.operator.equals(that.operator)
            && this.predicateNode.equalAst(that.predicateNode)
            && NodeUtil.equalAst(this.declarationList, that.declarationList);

    }
}
