package de.bmoth.modelchecker;

import com.microsoft.z3.Expr;

import java.util.Map;

/**
 * Created by krings on 28.04.17.
 */
public class State {
    State predecessor;
    Map<String,Expr> values;

    public State(State predecessor, Map<> values) {
        this.predecessor = predecessor;
        this.values = values;
    }
}
