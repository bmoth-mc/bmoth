package de.bmoth.modelchecker;

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
}
