package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomatonNode;

import java.util.*;

public class State {
    private final State predecessor;
    private final Map<String, Expr> values;
    private final Set<BuechiAutomatonNode> buechiNodes = new HashSet<>();

    public State(State predecessor, Map<String, Expr> values) {
        this.predecessor = predecessor;
        this.values = values;
    }

    public String toString() {
        return this.values.toString();
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        State that = (State) obj;
        return this.values.equals(that.values);
    }

    public BoolExpr getStateConstraint(Context context) {
        BoolExpr[] result = values.entrySet().stream().map(entry -> {
            Expr singleExpression = entry.getValue();
            Sort sort = singleExpression.getSort();
            Expr identifierExpr = context.mkConst(entry.getKey(), sort);
            return context.mkEq(identifierExpr, singleExpression);
        }).toArray(BoolExpr[]::new);

        switch (result.length) {
            case 0:
                return null;
            case 1:
                return result[0];
            default:
                return context.mkAnd(result);
        }
    }

    public List<String> getPath() {
        List<String> path = new ArrayList<>();
        for (State current = this.predecessor; current != null; current = current.predecessor) {
            path.add(current.toString());
        }
        return path;
    }

    public Map<String, Expr> getValues() {
        return values;
    }

    public Set<BuechiAutomatonNode> getBuechiNodes() {
        return buechiNodes;
    }

    public void addBuechiNodes(Set<BuechiAutomatonNode> buechiNodes) {
        this.buechiNodes.addAll(buechiNodes);
    }
}
