package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.QuantifiedPredicateContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantifiedPredicateNode extends PredicateNode {

    public static enum QuatifiedPredicateOperator {
        UNIVERSAL_QUANTIFICATION, EXISTENTIAL_QUANTIFICATION
    }

    private static final Map<Integer, QuatifiedPredicateOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.FOR_ANY, QuatifiedPredicateOperator.UNIVERSAL_QUANTIFICATION);
        map.put(BMoThParser.EXITS, QuatifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION);
    }

    private final List<DeclarationNode> declarationList;
    private final PredicateNode predicateNode;
    private QuatifiedPredicateOperator operator;

    public QuantifiedPredicateNode(QuantifiedPredicateContext ctx, List<DeclarationNode> declarationList,
                                   PredicateNode predNode) {
        this.declarationList = declarationList;
        this.predicateNode = predNode;
        this.operator = loopUpOperator(ctx.operator.getType());
    }

    private QuatifiedPredicateOperator loopUpOperator(int type) {
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

    public QuatifiedPredicateOperator getOperator() {
        return operator;
    }

}
