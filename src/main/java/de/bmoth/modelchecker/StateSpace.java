package de.bmoth.modelchecker;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class StateSpace {
    private final DirectedGraph<State, DefaultEdge> graph;
    private final Set<StateSpaceNode> spaceStateRoot;

    public StateSpace(Set<StateSpaceNode> spaceStateRoot) {
        this.spaceStateRoot = spaceStateRoot;
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

    public List<List<State>> getCycles() {
        return new TarjanSimpleCycles<>(graph).findSimpleCycles();
    }

    public Set<StateSpaceNode> getRoot() {
        return spaceStateRoot;
    }
}
