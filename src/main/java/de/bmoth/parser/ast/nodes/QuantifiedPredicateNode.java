package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.QuantifiedPredicateContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuantifiedPredicateNode extends PredicateNode {

    public static enum QuantifiedPredicateOperator {
        UNIVERSAL_QUANTIFICATION, EXISTENTIAL_QUANTIFICATION
    }

    private static final Map<Integer, QuantifiedPredicateOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.FOR_ANY, QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION);
        map.put(BMoThParser.EXITS, QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION);
    }

    private final List<DeclarationNode> declarationList;
    private final PredicateNode predicateNode;
    private QuantifiedPredicateOperator operator;

    public QuantifiedPredicateNode(QuantifiedPredicateContext ctx, List<DeclarationNode> declarationList,
                                   PredicateNode predNode) {
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = loopUpOperator(ctx.operator.getType());
    }

    private QuantifiedPredicateOperator loopUpOperator(int type) {
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
        switch (operator) {
            case EXISTENTIAL_QUANTIFICATION:
                sb.append("EXISTS(");
                break;
            case UNIVERSAL_QUANTIFICATION:
                sb.append("FORALL(");
                break;
        }
        sb.append(declarationList.stream().map(Object::toString).collect(Collectors.joining(",")));
        sb.append(",");
        sb.append(predicateNode);
        sb.append(")");
        return sb.toString();
    }
}
