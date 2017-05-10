package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

import java.util.Map;

public class State {
    State predecessor;
    Map<String, Expr> values;

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
        if (!(obj instanceof State)){
            return false;
        }
        if (this == obj){
            return true;
        }

        State that = (State) obj;
        return this.values.equals(that.values);
    }

    public BoolExpr getStateConstraint(Context context) {
        BoolExpr result = null;
        for (Map.Entry<String, Expr> entry : values.entrySet()) {
            Expr singleExpression =  entry.getValue();
            Sort sort = singleExpression.getSort();
            Expr identifierExpr = context.mkConst(entry.getKey(), sort);
            BoolExpr eq = context.mkEq(identifierExpr, singleExpression);
            if (result == null) {
                result = eq;
            } else {
                result = context.mkAnd(result, eq);
            }
        }
        return result;
    }
}
