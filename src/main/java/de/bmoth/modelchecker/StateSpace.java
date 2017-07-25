package de.bmoth.modelchecker;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class StateSpace extends DefaultDirectedGraph<State, DefaultEdge> {
    @Deprecated
    private final DirectedGraph<State, DefaultEdge> graph;
    @Deprecated
    private final Set<StateSpaceNode> _spaceStateRoot;

    private final Set<State> rootVertexSet;

    @Deprecated
    public StateSpace(Set<StateSpaceNode> spaceStateRoot) {
        super(DefaultEdge.class);
        this._spaceStateRoot = spaceStateRoot;
        this.rootVertexSet = null;
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // breadth-first generation of vertices
        Queue<StateSpaceNode> queue = new ArrayDeque<>();
        Set<StateSpaceNode> visited = new HashSet<>();

        // init queue
        for (StateSpaceNode root : spaceStateRoot) {
            queue.add(root);
            visited.add(root);
        }

        // process queue ...
        while (!queue.isEmpty()) {
            StateSpaceNode node = queue.poll();

            // ... store vertex
            graph.addVertex(node.getState());

            // ... process successors
            for (StateSpaceNode successorNode : node.getSuccessors()) {
                if (!visited.contains(successorNode)) {
                    queue.add(successorNode);
                    visited.add(successorNode);
                }

                // ... store edges
                graph.addVertex(successorNode.getState());
                graph.addEdge(node.getState(), successorNode.getState());
            }
        }
    }

    public StateSpace() {
        super(DefaultEdge.class);
        this._spaceStateRoot = null;
        this.graph = null;
        this.rootVertexSet = new LinkedHashSet<>();
    }

    @Deprecated
    public List<List<State>> _getCycles() {
        return new TarjanSimpleCycles<>(this).findSimpleCycles();
    }

    @Deprecated
    public Set<StateSpaceNode> _getRoot() {
        return _spaceStateRoot;
    }

    @Deprecated
    public DirectedGraph<State, DefaultEdge> _getGraph() {
        return graph;
    }

    public void addRootVertex(State state) {
        rootVertexSet.add(state);
        addVertex(state);
    }

    public Set<State> rootVertexSet() {
        return rootVertexSet;
    }
}
