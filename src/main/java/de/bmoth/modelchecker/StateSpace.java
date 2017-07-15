package de.bmoth.modelchecker;

import java.util.*;

public class StateSpace {
    private final Set<Vertex> V;
    private final Map<Vertex, Set<Vertex>> E;

    private int maxdfs;
    private Set<Vertex> U;
    private Stack<Vertex> S;

    public StateSpace(Set<StateSpaceNode> spaceStateRoot) {
        V = new HashSet<>();
        E = new HashMap<>();

        for (StateSpaceNode rootNode : spaceStateRoot) {
            collectVertices(null, new Vertex(rootNode));
        }
    }

    private void collectVertices(Vertex rootVertex, Vertex vertex) {
        if (!V.contains(vertex)) {
            V.add(vertex);
            for (StateSpaceNode succesor : vertex.stateSpaceNode.getSuccessors()) {
                collectVertices(vertex, new Vertex(succesor));
            }
        }

        if (!E.containsKey(rootVertex)){
            E.put(rootVertex, new HashSet<>());
        }

        Set<Vertex> successors = E.get(rootVertex);
        successors.add(vertex);
    }

    public void printStronglyConnectedComponents() {
        maxdfs = 0;
        U = new HashSet<>(V);
        S = new Stack<>();
        while (!U.isEmpty()) {
            Vertex v0 = U.iterator().next();
            tarjan(v0);
        }
    }

    private void tarjan(Vertex v) {
        v.dfs = maxdfs;
        v.lowlink = maxdfs;
        maxdfs++;

        S.push(v);
        U.remove(v);

        //for (Vertex v_ : v.stateSpaceNode.getSuccessors().stream().map()
        //    ) {

        //}
    }

    class Vertex {
        int dfs;
        int lowlink;
        StateSpaceNode stateSpaceNode;

        Vertex(StateSpaceNode stateSpaceNode) {
            this.stateSpaceNode = stateSpaceNode;
        }

        @Override
        public boolean equals(Object o) {
            return stateSpaceNode.equals(o);
        }

        @Override
        public int hashCode() {
            return stateSpaceNode.hashCode();
        }
    }
}
