package de.bmoth.modelchecker;

import java.util.Collections;
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

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StateSpaceNode)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        StateSpaceNode that = (StateSpaceNode) obj;
        return this.state.equals(that.state);
    }

    @Override
    public String toString() {
        return state.toString() + ", successors: " + successors.stream().map(successor -> successor.state).collect(Collectors.toList());
    }

    public Set<StateSpaceNode> getSuccessors() {
        return successors != null ? successors : Collections.emptySet();
    }
}
