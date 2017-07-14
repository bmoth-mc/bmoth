package de.bmoth.modelchecker;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StateSpaceNode {
    private final State state;
    private final Set<StateSpaceNode> successors;

    public StateSpaceNode(State state) {
        this.state = state;
        successors = new HashSet<>();
    }

    public void addSuccessor(StateSpaceNode successor) {
        successors.add(successor);
    }

    public Set<StateSpaceNode> getSuccessors() {
        return successors;
    }

    public State getState() {
        return state;
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
