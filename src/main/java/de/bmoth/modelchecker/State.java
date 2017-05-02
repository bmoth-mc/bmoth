package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

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

    @Deprecated
    public BoolExpr getValuesExpression(Context context) {
        BoolExpr result = null;
        for (Map.Entry<String, Expr> entry : values.entrySet()) {
            BoolExpr singleExpression = (BoolExpr) entry.getValue();

            if (result == null) {
                result = singleExpression;
            } else {
                result = context.mkAnd(result, singleExpression);
            }
        }
        return result;
    }
}
