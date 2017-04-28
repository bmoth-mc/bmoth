package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import java.util.Map;

/**
 * Created by krings on 28.04.17.
 * fixed by hansen on 28.04.17.
 */
public class State {
    State predecessor;
    Map<String, Expr> values;

    public State(State predecessor, Map<String, Expr> values) {
        this.predecessor = predecessor;
        this.values = values;
    }

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
