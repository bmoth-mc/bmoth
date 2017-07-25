package de.bmoth.modelchecker;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.LinkedHashSet;
import java.util.Set;

public class StateSpace extends DefaultDirectedGraph<State, DefaultEdge> {
    private final Set<State> rootVertexSet;

    public StateSpace() {
        super(DefaultEdge.class);
        this.rootVertexSet = new LinkedHashSet<>();
    }

    public void addRootVertex(State state) {
        rootVertexSet.add(state);
        addVertex(state);
    }

    public Set<State> rootVertexSet() {
        return rootVertexSet;
    }
}
