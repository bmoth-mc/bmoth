package de.bmoth.modelchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StateSpaceNode {
    private final State state;
    private final List<StateSpaceNode> successors;

    public StateSpaceNode(State state) {
        this.state = state;
        successors = new ArrayList<>();
    }

    public void addSuccessor(StateSpaceNode successor) {
        successors.add(successor);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return state.equals(o);
    }

    @Override
    public String toString() {
        return state.toString() + ", successors: " + successors.stream().map(successor -> successor.state).collect(Collectors.toList());
    }
}
